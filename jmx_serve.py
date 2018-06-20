'''
Taken from:
http://stackoverflow.com/users/1074592/fakerainbrigand
http://stackoverflow.com/questions/15401815/python-simplehttpserver
'''
import sys
import os
import time
import zipfile
import shutil

if sys.version_info < (3,):
    import SimpleHTTPServer as server
    import SocketServer as socketserver
    import urlparse as parse
else:
    from http import server
    from urllib import parse
    import socketserver

PORT = 20007

jarName = "jmx.jar"

def exit():
    print("exit!")
    sys.exit(-1)

class MyHandler(server.SimpleHTTPRequestHandler):
    def do_GET(self):

        # Parse query data to find out what was requested
        parsedParams = parse.urlparse(self.path)

        if parsedParams.path == "/mlet":
            self.send_response(200)
            self.send_header('Content-Type', 'application/xml')
            self.end_headers()

            self.wfile.write("<mlet code=com.demo.JMX archive=jmx.jar name=%f:name=125,id=125 ></mlet>" % time.time())
            self.wfile.close()

        elif parsedParams.path == "/" + jarName:
            # send index.html, but don't redirect
            self.send_response(200)
            self.send_header('Content-Type', 'application/octet-stream')
            self.end_headers()
            with open("./jmx.jar", 'rb') as fin:
                self.copyfile(fin, self.wfile)
                self.wfile.close()
        else:
            self.send_response(200)

if __name__ == "__main__":

    args = sys.argv[1:]

    if len(args) > 0:
        PORT = int(args[0])

    httpd = socketserver.TCPServer(("", PORT), MyHandler)

    sys.stdout.write("serving at port: %s\n" % PORT)
    httpd.serve_forever()