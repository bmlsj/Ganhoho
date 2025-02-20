#.yml을 참조하기 위해생성
# import yaml

# def load_yml_config():
#     with open('../backend/src/main/resources/application-ocr.yml', 'r') as file:
#         config = yaml.safe_load(file)
#     return config.get('ocr', {}).get('clova', {})

# config = load_yml_config()
# CLOVA_API_URL = config.get('url')
# CLOVA_SECRET_KEY = config.get('secret-key')