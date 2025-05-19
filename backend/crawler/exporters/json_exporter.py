"""
JSON 내보내기 모듈
"""
import json
from typing import List, Dict, Any
from ..models.problem import Problem

class JsonExporter:
    """문제 데이터를 JSON 형식으로 내보내는 클래스"""
    
    def __init__(self, filename: str = "problem_data.json"):
        self.filename = filename
    
    def export(self, problems: List[Problem]) -> None:
        """문제 목록을 JSON 파일로 내보내기"""
        problem_dicts = [problem.to_dict() for problem in problems]
        
        with open(self.filename, 'w', encoding='utf-8') as f:
            json.dump(problem_dicts, f, ensure_ascii=False, indent=2)
        
        print(f"총 {len(problems)}개 문제를 {self.filename}에 저장했습니다.")
