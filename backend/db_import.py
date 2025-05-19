"""
크롤링한 SQL 파일을 데이터베이스에 삽입하는 스크립트
"""

import os
import glob
import re
import argparse
import pymysql
from tqdm import tqdm
from dotenv import load_dotenv
from concurrent.futures import ThreadPoolExecutor, as_completed

def create_tables(conn):
    """기본 테이블 생성 - CodingProblem 테이블과 TestCase 테이블"""
    try:
        with conn.cursor() as cursor:
            # 코딩 문제 테이블 생성
            cursor.execute("""
            CREATE TABLE IF NOT EXISTS coding_problems (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                title VARCHAR(255) NOT NULL,
                description LONGTEXT,
                input_description LONGTEXT,
                output_description LONGTEXT,
                constraints LONGTEXT,
                difficulty INTEGER,
                time_limit_seconds INTEGER NOT NULL DEFAULT 1,
                memory_limit_mb INTEGER NOT NULL DEFAULT 256,
                sample_code LONGTEXT,
                created_at DATETIME NOT NULL,
                updated_at DATETIME NOT NULL,
                is_active BOOLEAN NOT NULL DEFAULT TRUE
            );
            """)
            
            # 테스트 케이스 테이블 생성
            cursor.execute("""
            CREATE TABLE IF NOT EXISTS test_cases (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                problem_id BIGINT NOT NULL,
                input LONGTEXT,
                expected_output LONGTEXT,
                is_sample BOOLEAN NOT NULL DEFAULT FALSE,
                is_hidden BOOLEAN NOT NULL DEFAULT FALSE,
                explanation LONGTEXT,
                test_number INTEGER,
                FOREIGN KEY (problem_id) REFERENCES coding_problems(id)
            );
            """)
            
            # 문제 카테고리 테이블 생성
            cursor.execute("""
            CREATE TABLE IF NOT EXISTS problem_categories (
                problem_id BIGINT NOT NULL,
                category VARCHAR(255) NOT NULL,
                PRIMARY KEY (problem_id, category),
                FOREIGN KEY (problem_id) REFERENCES coding_problems(id)
            );
            """)
            
            conn.commit()
            
            print("테이블 생성 완료")
            
        return True
            
    except Exception as e:
            
        print(f"테이블 생성 오류: {e}")
            
        return False

def import_sql_file(sql_file, conn, db_name):
    """SQL 파일의 내용을 데이터베이스에 실행"""
    try:
        # 파일 내용 읽기
        with open(sql_file, 'r', encoding='utf-8') as f:
            sql_content = f.read()
            
            # 테이블 생성 쿼리 여부 확인
            has_create_table = 'CREATE TABLE' in sql_content.upper()
            if not has_create_table:
                # 테이블이 없으면 테이블 생성
                print("테이블 생성문이 없습니다. 기본 테이블을 생성합니다...")
                create_tables(conn)
        
        # 주석 제거
        sql_content = re.sub(r'--.*?\n', '\n', sql_content)
        
        # SQL 문을 세미콜론으로 분리
        sql_statements = sql_content.split(';')
        
        with conn.cursor() as cursor:
            # 먼저 데이터베이스 명시적 선택
            cursor.execute(f"USE {db_name}")
            
            for statement in sql_statements:
                statement = statement.strip()
                if statement:  # 빈 문장이 아닌 경우만 실행
                    try:
                        # 실행할 쿼리 출력 (디버깅용)
                        print(f"\n실행할 쿼리:\n{statement[:200]}{'...' if len(statement) > 200 else ''}")
                        cursor.execute(statement)
                    except Exception as e:
                        print(f"\n쿼리 실행 중 오류:\n{statement[:200]}{'...' if len(statement) > 200 else ''}\n오류: {e}")
        
        conn.commit()
        return True
    except Exception as e:
        print(f"파일 {sql_file} 처리 중 오류: {e}")
        return False

def main():
    """메인 함수"""
    parser = argparse.ArgumentParser(description="SQL 파일을 데이터베이스에 삽입")
    
    parser.add_argument("--db-host", type=str, help="데이터베이스 호스트 (기본값: .env 파일)")
    parser.add_argument("--db-port", type=int, help="데이터베이스 포트 (기본값: .env 파일)")
    parser.add_argument("--db-user", type=str, help="데이터베이스 사용자 (기본값: .env 파일)")
    parser.add_argument("--db-password", type=str, help="데이터베이스 비밀번호 (기본값: .env 파일)")
    parser.add_argument("--db-name", type=str, help="데이터베이스 이름 (기본값: .env 파일)")
    parser.add_argument("--sql-dir", type=str, default="problems", help="SQL 파일이 있는 디렉토리")
    parser.add_argument("--file", type=str, help="직접 처리할 특정 SQL 파일 (파일 하나만 처리하고 싶을 때)")
    parser.add_argument("--pattern", type=str, default="*.sql", help="SQL 파일 패턴(예: baekjoon_*.sql)")
    parser.add_argument("--max-workers", type=int, default=4, help="병렬 처리에 사용할 최대 작업자 수")
    
    args = parser.parse_args()
    
    # .env 파일에서 환경 변수 로드
    load_dotenv()
    
    # 명령행 인수 또는 환경 변수에서 DB 설정 가져오기
    db_host = args.db_host or os.getenv('DB_HOST', 'localhost')
    db_port = args.db_port or int(os.getenv('DB_PORT', '3306'))
    db_user = args.db_user or os.getenv('DB_USERNAME', 'root')
    db_password = args.db_password or os.getenv('DB_PASSWORD', '')
    db_name = args.db_name or os.getenv('DB_DATABASE', '')
    
    print(f"\n데이터베이스 설정:")
    print(f"호스트: {db_host}")
    print(f"포트: {db_port}")
    print(f"사용자: {db_user}")
    print(f"데이터베이스: {db_name}")
    
    # 데이터베이스 연결 및 생성
    try:
        # 먼저 데이터베이스업이 연결
        conn_no_db = pymysql.connect(
            host=db_host,
            port=db_port,
            user=db_user,
            password=db_password,
            charset='utf8mb4'
        )
        
        # 데이터베이스 존재 확인 및 생성
        with conn_no_db.cursor() as cursor:
            cursor.execute(f"SHOW DATABASES LIKE '{db_name}'")
            if not cursor.fetchone():  # 데이터베이스가 없으면 생성
                print(f"데이터베이스 '{db_name}'이(가) 없습니다. 생성합니다...")
                cursor.execute(f"CREATE DATABASE {db_name}")
                conn_no_db.commit()
                print(f"데이터베이스 '{db_name}' 생성 완료")
            else:
                print(f"데이터베이스 '{db_name}'이(가) 존재합니다.")
        
        # 데이터베이스업이 연결 닫기
        conn_no_db.close()
        
        # 해당 데이터베이스에 연결
        conn = pymysql.connect(
            host=db_host,
            port=db_port,
            user=db_user,
            password=db_password,
            database=db_name,
            charset='utf8mb4'
        )
        print(f"데이터베이스 '{db_name}'에 연결했습니다.")
    except Exception as e:
        print(f"데이터베이스 연결 오류: {e}")
        return
    
    try:
        # 직접 특정 파일을 지정한 경우
        if args.file:
            if os.path.exists(args.file):
                sql_files = [args.file]
            else:
                print(f"'{args.file}' 파일을 찾을 수 없습니다.")
                return
        else:
            # SQL 파일 목록 가져오기
            sql_files = glob.glob(os.path.join(args.sql_dir, args.pattern))
            if not sql_files:
                print(f"'{args.sql_dir}' 디렉토리에 '{args.pattern}' 패턴과 일치하는 SQL 파일이 없습니다.")
                return
            
        print(f"총 {len(sql_files)}개의 SQL 파일을 찾았습니다.")
        
        # ThreadPoolExecutor로 병렬 처리
        success_count = 0
        max_workers = min(args.max_workers, len(sql_files))  # 파일 수보다 많은 작업자는 불필요
        
        print(f"병렬 작업 시작: {max_workers}개의 작업자 사용")
        
        # 각 SQL 파일을 별도의 연결로 처리
        def process_sql_file(sql_file):
            try:
                # 각 스레드별로 새 연결 생성
                thread_conn = pymysql.connect(
                    host=db_host,
                    port=db_port,
                    user=db_user,
                    password=db_password,
                    database=db_name,
                    charset='utf8mb4'
                )
                
                result = import_sql_file(sql_file, thread_conn, db_name)
                thread_conn.close()
                return result
            except Exception as e:
                print(f"파일 {sql_file} 처리 중 오류: {e}")
                return False
        
        with ThreadPoolExecutor(max_workers=max_workers) as executor:
            # 작업 제출
            future_to_file = {executor.submit(process_sql_file, sql_file): sql_file for sql_file in sql_files}
            
            # 결과 수집
            for future in tqdm(as_completed(future_to_file), total=len(sql_files), desc="SQL 파일 삽입 중"):
                sql_file = future_to_file[future]
                try:
                    if future.result():
                        success_count += 1
                except Exception as e:
                    print(f"파일 {sql_file} 처리 중 오류: {e}")
                
        print(f"총 {len(sql_files)}개 중 {success_count}개 SQL 파일을 성공적으로 삽입했습니다.")
    
    finally:
        # 데이터베이스 연결 종료
        conn.close()
        print("데이터베이스 연결을 종료했습니다.")

if __name__ == "__main__":
    main()
