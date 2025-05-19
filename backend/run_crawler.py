"""
백준 문제 크롤러 실행 스크립트
"""

import argparse
import os
import math
from crawler.crawlers.baekjoon_crawler import BaekjoonCrawler
from crawler.crawlers.programmers_crawler import ProgrammersCrawler
from crawler.exporters.json_exporter import JsonExporter
from crawler.exporters.sql_exporter import SqlExporter

def main():
    """크롤러 메인 함수"""
    parser = argparse.ArgumentParser(description="백준 및 프로그래머스 문제 크롤러")
    parser.add_argument("--boj-start", type=int, default=1, help="백준 시작 문제 ID")
    parser.add_argument("--boj-end", type=int, default=10000, help="백준 끝 문제 ID")
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
    
    # 백준 문제 크롤링 (배치 단위로 병렬화)
    if not args.no_boj:
        print(f"백준 문제 크롤링 (범위: {args.boj_start}~{args.boj_end})")
        
        # 문제 ID 범위를 batch_size 단위로 분할
        start_id = args.boj_start
        batch_count = math.ceil((args.boj_end - args.boj_start + 1) / args.batch_size)
        
        # 최대 병렬 작업은 배치 수와 지정된 max_workers 중 작은 값
        max_parallel = min(batch_count, args.max_workers)
        print(f"최대 병렬 작업 수: {max_parallel} (총 {batch_count}개 배치)")
        
        # 배치 정보 저장
        batches = []
        for batch in range(batch_count):
            batch_start = start_id + (batch * args.batch_size)
            batch_end = min(batch_start + args.batch_size - 1, args.boj_end)
            batches.append((batch_start, batch_end, batch+1, batch_count))
        
        baekjoon_crawler = BaekjoonCrawler()
        all_batch_problems = []
        
        # 배치 병렬 처리
        from concurrent.futures import ThreadPoolExecutor
        from tqdm import tqdm
        
        # 배치 처리 함수
        def process_batch(batch_info):
            batch_start, batch_end, batch_num, total_batches = batch_info
            print(f"백준 배치 {batch_num}/{total_batches} 크롤링 중 (범위: {batch_start}~{batch_end})")
            
            # 각 배치는 각 문제를 순차 처리 (병렬화는 배치 단위로 이루어짐)
            problems = baekjoon_crawler.crawl_problems(batch_start, batch_end, 1)
            
            # 배치 결과 저장
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
                
                print(f"백준 배치 {batch_num}/{total_batches} - {len(problems)}개 문제 크롤링 및 저장 완료")
                return problems
            else:
                print(f"백준 배치 {batch_num}/{total_batches} - 크롤링된 문제가 없습니다.")
                return []
                
        # 병렬 배치 처리 실행
        with ThreadPoolExecutor(max_workers=max_parallel) as executor:
            futures = [executor.submit(process_batch, batch_info) for batch_info in batches]
            for future in tqdm(as_completed(futures), total=len(batches), desc="백준 배치 처리"):
                try:
                    batch_problems = future.result()
                    all_batch_problems.extend(batch_problems)
                except Exception as e:
                    print(f"배치 처리 중 오류 발생: {e}")
        
        print(f"백준 문제 총 {len(all_batch_problems)}개 크롤링 완료")
        all_problems = all_batch_problems
    
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
