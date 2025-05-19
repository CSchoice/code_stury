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
            
            # 카테고리 삽입
            categories = self._extract_categories(problems)
            
            f.write("-- 카테고리 데이터\n")
            for i, category in enumerate(categories, 1):
                safe_category = category.replace("'", "''")
                f.write(f"INSERT INTO categories (id, name) VALUES ({i}, '{safe_category}');\n")
            
            f.write("\n-- 문제 데이터\n")
            for i, problem in enumerate(problems, 1):
                self._write_problem_sql(f, i, problem)
            
            f.write("\n-- 테스트 케이스 데이터\n")
            test_case_id = 1
            for i, problem in enumerate(problems, 1):
                for test_case in problem.test_cases:
                    self._write_test_case_sql(f, test_case_id, i, test_case)
                    test_case_id += 1
            
            category_map = {cat: i for i, cat in enumerate(categories, 1)}
            f.write("\n-- 문제-카테고리 연결 데이터\n")
            rel_id = 1
            
            for i, problem in enumerate(problems, 1):
                for category in problem.categories:
                    cat_id = category_map.get(category)
                    if cat_id:
                        f.write(f"INSERT INTO problem_categories (problem_id, category_id) VALUES ({i}, {cat_id});\n")
                        rel_id += 1
        
        print(f"SQL 삽입문을 {self.filename}에 저장했습니다.")
    
    def _extract_categories(self, problems: List[Problem]) -> Set[str]:
        """모든 문제에서 카테고리 추출"""
        categories = set()
        for problem in problems:
            for category in problem.categories:
                categories.add(category)
        return categories
    
    def _write_problem_sql(self, file, id: int, problem: Problem) -> None:
        """문제 SQL 삽입문 작성"""
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
        
        sql = f"""INSERT INTO coding_problems (
            id, title, description, input_description, output_description, 
            constraints, difficulty, time_limit_seconds, memory_limit_mb, 
            sample_code, created_at, updated_at, is_active
        ) VALUES (
            {id}, '{title}', '{description}', '{input_desc}', '{output_desc}', 
            '{constraints}', '{difficulty}', {time_limit}, {memory_limit}, 
            '{sample_code}', '{created_at}', '{updated_at}', {is_active}
        );\n"""
        file.write(sql)
    
    def _write_test_case_sql(self, file, id: int, problem_id: int, test_case: TestCase) -> None:
        """테스트 케이스 SQL 삽입문 작성"""
        test_number = test_case.test_number
        input_text = test_case.input.replace("'", "''")
        expected_output = test_case.expected_output.replace("'", "''")
        is_sample = 1 if test_case.is_sample else 0
        is_hidden = 1 if test_case.is_hidden else 0
        explanation = (test_case.explanation or "").replace("'", "''")
        
        sql = f"""INSERT INTO test_cases (
            id, problem_id, test_number, input, expected_output, is_sample, is_hidden, explanation
        ) VALUES (
            {id}, {problem_id}, {test_number}, '{input_text}', '{expected_output}', 
            {is_sample}, {is_hidden}, '{explanation}'
        );\n"""
        file.write(sql)
