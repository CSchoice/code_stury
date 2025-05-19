# python run_crawler.py --boj-start 1000 --boj-end 10000 --batch-size 100 --no-programmers --max-workers 4

"""
크롤러 메인 실행 파일
"""
import argparse
from .crawlers.baekjoon_crawler import BaekjoonCrawler
from .crawlers.programmers_crawler import ProgrammersCrawler
from .exporters.json_exporter import JsonExporter
from .exporters.sql_exporter import SqlExporter

import os
import math

def main():
    """크롤러 메인 함수"""
    parser = argparse.ArgumentParser(description="백준 및 프로그래머스 문제 크롤러")
    parser.add_argument("--boj-start", type=int, default=1, help="백준 시작 문제 ID")
    parser.add_argument("--boj-end", type=int, default=100000, help="백준 끝 문제 ID")
    parser.add_argument("--batch-size", type=int, default=100, help="문제를 나눌 배치 크기 (기본 100개)")
    parser.add_argument("--output-dir", type=str, default="problems", help="출력 디렉토리")
    parser.add_argument("--pgs-limit", type=int, default=10, help="프로그래머스 문제 개수 제한 (기본 10개)")
    parser.add_argument("--no-boj", action="store_true", help="백준 문제 크롤링 건너뛰기")
    parser.add_argument("--no-programmers", action="store_true", help="프로그래머스 문제 크롤링 건너뛰기")
    parser.add_argument("--max-workers", type=int, default=4, help="병렬 작업 스레드 수 (기본 4개)")
    
    args = parser.parse_args()
    
    # 출력 디렉토리 생성
    os.makedirs(args.output_dir, exist_ok=True)
    
    print("문제 크롤링을 시작합니다...")
    all_problems = []
    
    # 백준 문제 크롤링 (100개씩 분할)
    if not args.no_boj:
        print(f"백준 문제 크롤링 (범위: {args.boj_start}~{args.boj_end})")
        
        # 문제 ID 범위를 batch_size 단위로 분할
        start_id = args.boj_start
        batch_count = math.ceil((args.boj_end - args.boj_start + 1) / args.batch_size)
        
        baekjoon_crawler = BaekjoonCrawler()
        
        for batch in range(batch_count):
            batch_start = start_id + (batch * args.batch_size)
            batch_end = min(batch_start + args.batch_size - 1, args.boj_end)
            
            print(f"백준 배치 {batch+1}/{batch_count} 크롤링 중 (범위: {batch_start}~{batch_end})")
            
            # 현재 배치 크롤링
            problems = baekjoon_crawler.crawl_problems(batch_start, batch_end, args.max_workers)
            if problems:
                # 현재 배치 저장
                batch_json_file = os.path.join(args.output_dir, f"baekjoon_{batch_start}_{batch_end}.json")
                batch_sql_file = os.path.join(args.output_dir, f"baekjoon_{batch_start}_{batch_end}.sql")
                
                # JSON 내보내기
                json_exporter = JsonExporter(batch_json_file)
                json_exporter.export(problems)
                
                # SQL 내보내기
                sql_exporter = SqlExporter(batch_sql_file)
                sql_exporter.export(problems)
                
                all_problems.extend(problems)
                print(f"백준 배치 {batch+1}/{batch_count} - {len(problems)}개 문제 크롤링 및 저장 완료")
            else:
                print(f"백준 배치 {batch+1}/{batch_count} - 크롤링된 문제가 없습니다.")
        
        print(f"백준 문제 총 {len(all_problems)}개 크롤링 완료")
    
    # 프로그래머스 문제 크롤링
    programmers_problems = []
    if not args.no_programmers:
        print(f"프로그래머스 문제 크롤링 (최대 {args.pgs_limit}개)")
        programmers_crawler = ProgrammersCrawler()
        programmers_problems = programmers_crawler.crawl_problems(args.pgs_limit, args.max_workers)
        
        if programmers_problems:
            # 파일 저장
            pgs_json_file = os.path.join(args.output_dir, f"programmers.json")
            pgs_sql_file = os.path.join(args.output_dir, f"programmers.sql")
            
            # JSON 내보내기
            json_exporter = JsonExporter(pgs_json_file)
            json_exporter.export(programmers_problems)
            
            # SQL 내보내기
            sql_exporter = SqlExporter(pgs_sql_file)
            sql_exporter.export(programmers_problems)
            
            all_problems.extend(programmers_problems)
            print(f"프로그래머스 문제 {len(programmers_problems)}개 크롤링 및 저장 완료")
        else:
            print("프로그래머스 문제 크롤링 결과가 없습니다.")
    
    # 통합 파일 생성하지 않음
    if all_problems:
        print(f"총 {len(all_problems)}개 문제를 {args.batch_size}개씩 저장했습니다.")
    else:
        print("크롤링된 문제가 없습니다.")

if __name__ == "__main__":
    main()
