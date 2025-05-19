"""
백준 온라인 저지 문제 크롤러
"""
import time
import requests
import re
from bs4 import BeautifulSoup
from tqdm import tqdm
from typing import List, Optional, Dict, Any
from concurrent.futures import ThreadPoolExecutor, as_completed

from ..models.problem import Problem, TestCase

class BaekjoonCrawler:
    """백준 온라인 저지 문제 크롤러"""
    
    BASE_URL = "https://www.acmicpc.net/problem/"
    SOLVED_API_URL = "https://solved.ac/api/v3/problem/show?problemId="
    
    def __init__(self):
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
    
    def get_problem_difficulty(self, problem_id: int) -> tuple:
        """solved.ac API를 사용하여 문제 난이도와 태그 가져오기"""
        try:
            response = requests.get(f"{self.SOLVED_API_URL}{problem_id}", headers=self.headers)
            if response.status_code == 200:
                data = response.json()
                level = data.get('level', 0)  # 실제 난이도 (1~30)
                
                # 난이도를 수치화하여 그대로 저장
                difficulty = level
                
                # 태그 추출
                tags = []
                for tag in data.get('tags', []):
                    # 'displayNames'의 첫 번째 항목의 'name' 추출 (한국어 태그명)
                    display_names = tag.get('displayNames', [])
                    if display_names:
                        tag_name = display_names[0].get('name', '')
                        if tag_name:
                            tags.append(tag_name)
                
                return difficulty, tags, level
            
        except Exception as e:
            print(f"Solved.ac API 오류: {e}")
        
        # 기본값: 레벨 0(난이도 없음), 빈 태그 목록
        return 0, [], 0
    
    def parse_test_cases(self, soup: BeautifulSoup) -> List[TestCase]:
        """예제 입출력을 테스트 케이스로 변환"""
        test_cases = []
        
        for i in range(1, 6):  # 최대 5개 예제
            input_elem = soup.select_one(f'#sample-input-{i}')
            output_elem = soup.select_one(f'#sample-output-{i}')
            
            if input_elem and output_elem:
                input_text = input_elem.text.strip()
                output_text = output_elem.text.strip()
                
                test_case = TestCase(
                    input=input_text,
                    expected_output=output_text,
                    test_number=i,
                    is_sample=True,
                    is_hidden=False
                )
                test_cases.append(test_case)
            else:
                break
        
        return test_cases
    
    def crawl_problem(self, problem_id: int) -> Optional[Problem]:
        """특정 ID의 백준 문제 크롤링"""
        try:
            url = f"{self.BASE_URL}{problem_id}"
            response = requests.get(url, headers=self.headers)
            
            if response.status_code != 200:
                return None
            
            soup = BeautifulSoup(response.text, 'html.parser')
            # print(soup)
            # 제목 추출
            title_elem = soup.select_one('#problem_title')
            if not title_elem:
                return None
            title = title_elem.text.strip()
            
            # 문제 설명 추출
            description = ""
            description_elem = soup.select_one('#problem_description')
            if description_elem:
                description = description_elem.text.strip()
            
            # 입력 설명 추출
            input_desc = ""
            input_elem = soup.select_one('#problem_input')
            if input_elem:
                input_desc = input_elem.text.strip()
            
            # 출력 설명 추출
            output_desc = ""
            output_elem = soup.select_one('#problem_output')
            if output_elem:
                output_desc = output_elem.text.strip()
            
            # 테스트 케이스 추출
            test_cases = self.parse_test_cases(soup)
            
            # 난이도 및 태그 가져오기
            difficulty, categories, level = self.get_problem_difficulty(problem_id)
            
            # 문제 객체 생성
            problem = Problem(
                title=title,
                description=description,
                source="BOJ",
                external_id=str(problem_id),
                url=url,
                difficulty=difficulty,
                categories=categories,
                input_description=input_desc,
                output_description=output_desc,
                constraints="",  # 백준은 제약 조건이 분리되어 있지 않음
                sample_code=None,  # 백준은 기본 예제 코드를 제공하지 않음
                test_cases=test_cases
            )
            
            # 메모리 및 시간 제한 설정 - 테이블에서 추출
            time_limit = 10  # 기본값
            memory_limit = 256  # 기본값
            
            # 테이블에서 시간과 메모리 제한 추출
            info_table = soup.select_one('#problem-info')
            if info_table:
                # 테이블의 tbody > tr > td 엘리먼트를 가져옴
                tds = info_table.select('tbody > tr > td')
                if len(tds) >= 2:
                    # 첫 번째 td에 시간 제한
                    time_text = tds[0].text.strip()
                    time_match = re.search(r'(\d+)', time_text)
                    if time_match:
                        time_limit = int(time_match.group(1))
                    
                    # 두 번째 td에 메모리 제한
                    memory_text = tds[1].text.strip()
                    memory_match = re.search(r'(\d+)', memory_text)
                    if memory_match:
                        memory_limit = int(memory_match.group(1))
                    
                    print(f"Problem {problem_id} - Time: {time_limit}s, Memory: {memory_limit}MB (From table)")
            
            # 추출한 값 적용
            problem.time_limit_seconds = time_limit
            problem.memory_limit_mb = memory_limit
            
            return problem
            
        except Exception as e:
            print(f"백준 문제 {problem_id} 처리 중 오류: {e}")
            return None
    
    def crawl_problems(self, start_id: int, end_id: int, max_workers: int = 4) -> List[Problem]:
        """지정된 범위의 백준 문제를 병렬로 크롤링"""
        problems = []
        problem_ids = list(range(start_id, end_id + 1))
        
        with ThreadPoolExecutor(max_workers=max_workers) as executor:
            # 작업 제출
            future_to_id = {executor.submit(self.crawl_problem, pid): pid for pid in problem_ids}
            
            # 결과 수집 (tqdm으로 진행 상황 표시)
            for future in tqdm(as_completed(future_to_id), total=len(problem_ids), desc="백준 문제 크롤링"):
                problem_id = future_to_id[future]
                try:
                    problem = future.result()
                    if problem:
                        problems.append(problem)
                except Exception as e:
                    print(f"백준 문제 {problem_id} 처리 중 오류: {e}")
        
        # 문제 ID 순으로 정렬
        problems.sort(key=lambda p: int(p.external_id))
        return problems
