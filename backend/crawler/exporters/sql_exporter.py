"""
SQL 내보내기 모듈
"""
from datetime import datetime
from typing import List, Dict, Any, Set
from ..models.problem import Problem, TestCase

class SqlExporter:
    """문제 데이터를 SQL 삽입문으로 내보내는 클래스"""
    
    def __init__(self, filename: str = "problem_insert.sql"):
        self.filename = filename
    
    def export(self, problems: List[Problem]) -> None:
        """문제 목록을 SQL 파일로 내보내기"""
        with open(self.filename, 'w', encoding='utf-8') as f:
            f.write("-- 백준 & 프로그래머스 문제 데이터\n")
            f.write("-- 생성일: " + datetime.now().strftime("%Y-%m-%d %H:%M:%S") + "\n\n")
            
            # 카테고리 추출 (삽입은 하지 않음 - 이제 problem_categories에 직접 이름 저장)
            categories = self._extract_categories(problems)
            
            f.write("-- 카테고리 정보\n")
            f.write(f"-- 총 {len(categories)}개의 카테고리: {', '.join(sorted(categories))}\n\n")
            
            f.write("\n-- 문제 데이터\n")
            
            # AUTO_INCREMENT 설정 관련 코드 추가
            f.write("-- ID 값의 자동 증가를 활성화하고 LAST_INSERT_ID() 함수를 사용하여 카테고리 연결\n")
            
            for i, problem in enumerate(problems):
                # 외부 ID를 정수로 변환
                external_id = problem.external_id
                problem_id = int(external_id) if external_id.isdigit() else 0
                
                # 문제 SQL 작성 (external_id를 ID로 사용)
                self._write_problem_sql(f, problem)
                
                # 현재 문제의 카테고리 바로 삽입 (external_id 사용)
                if problem.categories:
                    f.write("\n-- 해당 문제의 카테고리 연결\n")
                    for category in problem.categories:
                        safe_category = category.replace("'", "''")
                        f.write(f"INSERT INTO problem_categories (problem_id, category) VALUES ({problem_id}, '{safe_category}');\n")
                
                # 테스트 케이스 바로 삽입 (external_id 사용)
                if problem.test_cases:
                    f.write("\n-- 해당 문제의 테스트 케이스\n")
                    for j, test_case in enumerate(problem.test_cases):
                        self._write_test_case_sql(f, problem_id, j+1, test_case)
        
        print(f"SQL 삽입문을 {self.filename}에 저장했습니다.")
    
    def _extract_categories(self, problems: List[Problem]) -> Set[str]:
        """모든 문제에서 카테고리 추출"""
        categories = set()
        for problem in problems:
            for category in problem.categories:
                categories.add(category)
        return categories
    
    def _write_problem_sql(self, file, problem: Problem) -> None:
        """문제 SQL 삽입문 작성 (externalId를 ID로 사용)"""
        source = problem.source
        external_id = problem.external_id.replace("'", "''")
        title = problem.title.replace("'", "''")
        description = problem.description.replace("'", "''")
        input_desc = (problem.input_description or "").replace("'", "''")
        output_desc = (problem.output_description or "").replace("'", "''")
        constraints = (problem.constraints or "").replace("'", "''")
        difficulty = problem.difficulty
        time_limit = problem.time_limit_seconds
        memory_limit = problem.memory_limit_mb
        sample_code = (problem.sample_code or "").replace("'", "''")
        created_at = problem.created_at
        updated_at = problem.updated_at
        is_active = 1 if problem.is_active else 0
        
        # external_id를 정수로 변환하여 ID로 사용
        problem_id = int(external_id) if external_id.isdigit() else 0
        
        sql = f"""INSERT INTO coding_problems (
            id, title, description, input_description, output_description, 
            constraints, difficulty, time_limit_seconds, memory_limit_mb, 
            sample_code, created_at, updated_at, is_active
        ) VALUES (
            {problem_id}, '{title}', '{description}', '{input_desc}', '{output_desc}', 
            '{constraints}', '{difficulty}', {time_limit}, {memory_limit}, 
            '{sample_code}', '{created_at}', '{updated_at}', {is_active}
        );
"""
        file.write(sql)
    
    def _write_test_case_sql(self, file, problem_id: int, test_number: int, test_case: TestCase) -> None:
        """테스트 케이스 SQL 삽입문 작성 (problem_id 직접 사용)"""
        test_number = test_case.test_number or test_number
        input_text = (test_case.input or "").replace("'", "''")
        expected_output = (test_case.expected_output or "").replace("'", "''")
        is_sample = 1 if test_case.is_sample else 0
        is_hidden = 1 if test_case.is_hidden else 0
        explanation = (test_case.explanation or "").replace("'", "''")
        
        sql = f"""INSERT INTO test_cases (
            problem_id, test_number, input, expected_output, is_sample, is_hidden, explanation
        ) VALUES (
            {problem_id}, {test_number}, '{input_text}', '{expected_output}', {is_sample}, {is_hidden}, '{explanation}'
        );
"""
        file.write(sql)
