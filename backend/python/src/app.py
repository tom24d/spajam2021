import os
import json

from bottle import run, request, debug, response, Bottle

debug(True)

serve_port = os.environ['PORT']

app = Bottle()

@app.get('/ping')
def ping():
    return json.dumps({"response": "pong"})


run(app, host="0.0.0.0", port=serve_port)
