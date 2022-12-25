import json


def lambda_handler(event, context):
    first_name = event['first_name']
    last_name = event['last_name']
    greeting = event['greeting']

    message = f"{greeting} {first_name} {last_name}!"

    return {
        'message': message
    }
