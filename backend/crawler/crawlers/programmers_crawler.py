"""
프로그래머스 문제 크롤러
"""
import time
import requests
from bs4 import BeautifulSoup
from tqdm import tqdm
from typing import List, Optional, Dict, Any
from concurrent.futures import ThreadPoolExecutor, as_completed

from ..models.problem import Problem, TestCase

class ProgrammersCrawler:
    """프로그래머스 문제 크롤러"""
    
    BASE_URL = "https://school.programmers.co.kr/learn/courses/30/lessons/"
    
    def __init__(self):
        self.headers = {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
        }
        
        # 크롤링 가능한 문제 ID 목록
        self.problem_list = [
            {"id": "42576", "title": "완주하지 못한 선수", "level": 1, "category": "해시"},
            {"id": "42577", "title": "전화번호 목록", "level": 2, "category": "해시"},
            {"id": "42578", "title": "위장", "level": 2, "category": "해시"},
            {"id": "42579", "title": "베스트앨범", "level": 3, "category": "해시"},
            {"id": "42583", "title": "다리를 지나는 트럭", "level": 2, "category": "스택/큐"},
            {"id": "42584", "title": "주식가격", "level": 2, "category": "스택/큐"},
            {"id": "42586", "title": "기능개발", "level": 2, "category": "스택/큐"},
            {"id": "42587", "title": "프린터", "level": 2, "category": "스택/큐"},
            {"id": "42626", "title": "더 맵게", "level": 2, "category": "힙"},
            {"id": "42627", "title": "디스크 컨트롤러", "level": 3, "category": "힙"},
            {"id": "42628", "title": "이중우선순위큐", "level": 3, "category": "힙"},
            {"id": "42746", "title": "가장 큰 수", "level": 2, "category": "정렬"},
            {"id": "42747", "title": "H-Index", "level": 2, "category": "정렬"},
            {"id": "42748", "title": "K번째수", "level": 1, "category": "정렬"},
            {"id": "42839", "title": "소수 찾기", "level": 2, "category": "완전탐색"},
            {"id": "42840", "title": "모의고사", "level": 1, "category": "완전탐색"},
            {"id": "42842", "title": "카펙", "level": 2, "category": "완전탐색"},
            {"id": "43105", "title": "정수 삼각형", "level": 3, "category": "동적계획법"},
            {"id": "43162", "title": "네트워크", "level": 3, "category": "깊이/너비 우선 탐색"},
            {"id": "43165", "title": "타겟 넘버", "level": 2, "category": "깊이/너비 우선 탐색"}
        ]
    
    def parse_test_cases(self, soup: BeautifulSoup) -> List[TestCase]:
        """예제 입출력을 테스트 케이스로 변환"""
        test_cases = []
        
        # 입출력 예제 추출
        examples_inputs = []
        examples_outputs = []
        examples_elem = soup.select('.example-io')
        
        for i, elem in enumerate(examples_elem):
            if i % 2 == 0 and elem:  # 짝수 인덱스는 입력
                examples_inputs.append(elem.text.strip())
            elif elem:  # 홀수 인덱스는 출력
                examples_outputs.append(elem.text.strip())
        
        # 테스트 케이스 생성
        for i, (input_text, output_text) in enumerate(zip(examples_inputs, examples_outputs), 1):
            test_case = TestCase(
                input=input_text,
                expected_output=output_text,
                test_number=i,
                is_sample=True,
                is_hidden=False
            )
            test_cases.append(test_case)
        
        return test_cases
    
    def crawl_problem(self, problem_info: Dict[str, Any]) -> Optional[Problem]:
        """특정 ID의 프로그래머스 문제 크롤링"""
        try:
            problem_id = problem_info["id"]
            url = f"{self.BASE_URL}{problem_id}"
            
            response = requests.get(url, headers=self.headers)
            if response.status_code != 200:
                print(f"프로그래머스 문제 {problem_id} 접근 불가")
                return None
            
            soup = BeautifulSoup(response.text, 'html.parser')
            
            # 문제 설명 추출
            description = ""
            desc_elem = soup.select_one('.guide-section')
            if desc_elem:
                description = desc_elem.text.strip()
            
            # 제약사항 추출
            constraints = ""
            const_elems = soup.select('.guide-section-description')
            for elem in const_elems:
                if "제한사항" in elem.text:
                    constraints = elem.text.strip()
                    break
            
            # 테스트 케이스 추출
            test_cases = self.parse_test_cases(soup)
            
            # 문제 객체 생성
            problem = Problem(
                title=problem_info["title"],
                description=description,
                source="PGS",
                external_id=problem_id,
                url=url,
                difficulty=problem_info["level"],
                categories=[problem_info["category"]],
                constraints=constraints,
                test_cases=test_cases,
                time_limit_seconds=5,  # 프로그래머스 기본값
                memory_limit_mb=128    # 프로그래머스 기본값
            )
            
            # 초기 코드 추출
            sample_code = ""
            code_elem = soup.select_one('.CodeMirror-code')
            if code_elem:
                sample_code = code_elem.text.strip()
                problem.sample_code = sample_code
            
            return problem
            
        except Exception as e:
            print(f"프로그래머스 문제 {problem_id} 처리 중 오류: {e}")
            return None
    
    def crawl_problems(self, limit: int = None, max_workers: int = 2) -> List[Problem]:
        """프로그래머스 문제를 병렬로 크롤링"""
        problems = []
        problem_list = self.problem_list[:limit] if limit else self.problem_list
        
        with ThreadPoolExecutor(max_workers=max_workers) as executor:
            # 작업 제출
            future_to_problem = {executor.submit(self.crawl_problem, problem_info): problem_info for problem_info in problem_list}
            
            # 결과 수집 (tqdm으로 진행 상황 표시)
            for future in tqdm(as_completed(future_to_problem), total=len(problem_list), desc="프로그래머스 문제 크롤링"):
                problem_info = future_to_problem[future]
                try:
                    problem = future.result()
                    if problem:
                        problems.append(problem)
                except Exception as e:
                    print(f"프로그래머스 문제 {problem_info['id']} 처리 중 오류: {e}")
        
        # 문제 ID 순으로 정렬
        problems.sort(key=lambda p: p.external_id)
        return problems
