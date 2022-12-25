from urllib import request
from urllib import parse


def lambda_handler(event, context):
    url = "http://ec2-3-88-137-30.compute-1.amazonaws.com/api/crawl"

    payload = parse.urlencode({
        "indexName": "cubanews",
        "baseUrls": [
            "https://adncuba.com/noticias-de-cuba",
            "https://www.14ymedio.com/",
            "https://www.cibercuba.com/noticias"
        ],
        "limit": 10
    }).encode('ascii')

    try:
        resp = request.urlopen(url, payload)
        print(resp)
        return {
            'response': 'xxx'
        }
    except Exception as e:
        print('Error!!')
        print(e)
        raise
