"""
문제 및 테스트케이스 모델 정의
"""
from datetime import datetime
from dataclasses import dataclass, field
from typing import List, Optional
import json


@dataclass
class TestCase:
    """테스트 케이스 모델"""
    input: str
    expected_output: str
    test_number: int
    is_sample: bool = True
    is_hidden: bool = False
    explanation: Optional[str] = None


@dataclass
class Problem:
    """코딩 문제 모델 - CodingProblem 엔티티 구조에 맞춤"""
    title: str
    description: str
    source: str  # 'BOJ' 또는 'PGS'
    external_id: str
    url: str
    difficulty: str  # 'EASY', 'MEDIUM', 'HARD', 'VERY_HARD'
    categories: List[str] = field(default_factory=list)
    input_description: Optional[str] = None
    output_description: Optional[str] = None
    constraints: Optional[str] = None
    sample_code: Optional[str] = None
    time_limit_seconds: int = 10  # 기본값 10초
    memory_limit_mb: int = 256  # 기본값 256MB
    is_active: bool = True
    test_cases: List[TestCase] = field(default_factory=list)
    created_at: str = field(default_factory=lambda: datetime.now().isoformat())
    updated_at: str = field(default_factory=lambda: datetime.now().isoformat())
    
    def to_dict(self) -> dict:
        """모델을 딕셔너리로 변환"""
        result = {
            "title": self.title,
            "description": self.description,
            "source": self.source,
            "externalId": self.external_id,
            "url": self.url,
            "difficulty": self.difficulty,
            "inputDescription": self.input_description,
            "outputDescription": self.output_description,
            "constraints": self.constraints,
            "sampleCode": self.sample_code,
            "timeLimitSeconds": self.time_limit_seconds,
            "memoryLimitMb": self.memory_limit_mb,
            "isActive": self.is_active,
            "categories": self.categories,
            "createdAt": self.created_at,
            "updatedAt": self.updated_at,
            "metadata": json.dumps({
                "source": self.source,
                "externalId": self.external_id,
                "categoryCount": len(self.categories),
                "testCaseCount": len(self.test_cases)
            }, ensure_ascii=False)
        }
        return result
    
    def map_difficulty(self) -> None:
        """난이도를 CodingProblem 엔티티 구조의 Difficulty 열거형에 맞게 변환"""
        # 숫자형 난이도를 문자열 난이도로 변환
        if isinstance(self.difficulty, int):
            if self.difficulty <= 1:
                self.difficulty = "EASY"
            elif self.difficulty == 2:
                self.difficulty = "MEDIUM"
            elif self.difficulty == 3:
                self.difficulty = "HARD"
            else:
                self.difficulty = "VERY_HARD"
