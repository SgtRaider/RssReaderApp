from flask import Flask
import json
import MySQLdb as mysql

app = Flask(__name__)

@app.route('/api')
def api_root():
    return 'Welcome'


@app.route('/getrss', methods=['GET', 'POST'])
def urlgetallrss():
    return getallrss()


@app.route('/getrss/topic=<string:topic>', methods=['GET', 'POST'])
def urlgetrsstopic(topic):
    return getrsstopic(topic)


@app.route('/getrss/user/id=<int:id>&topic=<string:topic>', methods=['GET', 'POST'])
def urlgetusersrssbytopic(id, topic):
    return getusersrssbytopic(id, topic)


@app.route('/getrss/user/id=<int:id>', methods=['GET', 'POST'])
def urlgetusersrss(id):
    return getusersrss(id)


@app.route('/getrss/nouser/id=<int:id>', methods=['GET', 'POST'])
def urlgetnousersrss(id):
    return getnousersrss(id)


@app.route('/setrss/cname=<string:cname>&url=<string:url>&topic=<string:topic>', methods=['GET', 'POST'])
def urlsetrss(cname, url, topic):
    data = {}
    data['insert'] = setrss(cname, url, topic)
    return json.dumps(data)


@app.route('/setuser_rss/id_user=<int:id_user>&id_rss=<int:id_rss>', methods=['GET', 'POST'])
def urlsetrss_user(id_user, id_rss):
    data = {}
    data['insert'] = setuser_rss(id_user, id_rss)
    return json.dumps(data)


@app.route('/setuser/name=<string:name>&surname=<string:surname>&nick=<string:nick>&password=<string:password>&mail=<string:mail>', methods=['GET', 'POST'])
def urlsetuser(name, surname, nick, password, mail):
    data = {}
    data['insert'] = setuser(name, surname, nick, password, mail)
    return json.dumps(data)


@app.route('/user/getid/nickname=<string:nickname>', methods=['GET', 'POST'])
def usergetid(nickname):
    data = {}
    data['id'] = getuserid(nickname)
    return json.dumps(data)


@app.route('/checkuser/nick=<string:nick>&password=<string:password>', methods=['GET', 'POST'])
def urlcheckuser(nick, password):
    data = {}
    data['check'] = checkuser(nick, password)
    return json.dumps(data)


@app.route('/delete/user_rss/id_rss=<int:idrss>&id_user=<int:iduser>', methods=['GET', 'POST'])
def removeuserrss(idrss, iduser):
    data = {}
    data['removed'] = removeuserrss(iduser, idrss)
    return json.dumps(data)


def getrsstopic(topic):
    cnx = connect()
    cursor = cnx.cursor()
    cursor.execute("select id, company_name, url, topic from rss where topic = '%s'" % (topic))
    results = cursor.fetchall()
    rss = {}
    data = {}
    datos = []
    for row in results:
        data['id'] = row[0]
        data['company_name'] = row[1]
        data['url'] = row[2]
        data['topic'] = row[3]
        datos.append(data)
        data = {}
    rss['rss'] = datos
    cnx.close()
    return json.dumps(rss)


def getallrss():
    cnx = connect()
    cursor = cnx.cursor()
    cursor.execute("select id, company_name, url, topic from rss")
    results = cursor.fetchall()
    rss = {}
    data = {}
    datos = []
    for row in results:
        data['id'] = row[0]
        data['company_name'] = row[1]
        data['url'] = row[2]
        data['topic'] = row[3]
        datos.append(data)
        data = {}
    rss['rss'] = datos
    cnx.close()
    return json.dumps(rss)


def getnousersrss(id):
    cnx = connect()
    cursor = cnx.cursor()
    cursor.execute("select r.id, r.company_name, r.url, r.topic from rss r where r.id not in (SELECT id_rss FROM user_rss WHERE id_user = '%d')" % (id))
    results = cursor.fetchall()
    rss = {}
    data = {}
    datos = []
    for row in results:
        data['id'] = row[0]
        data['company_name'] = row[1]
        data['url'] = row[2]
        data['topic'] = row[3]
        datos.append(data)
        data = {}
    rss['rss'] = datos
    cnx.close()
    return json.dumps(rss)


def setrss(cname, url, topic):
    print cname + ' ' + url + ' ' + topic
    cnx = connect()
    cursor = cnx.cursor()
    try:
        cursor.execute("INSERT INTO rss (company_name, url, topic) VALUES ('%s','%s','%s')" % (cname, url, topic))
        cnx.commit()
    except:
        cnx.rollback()
        return False
    return True
    cnx.close


def getusersrss(id):
    cnx = connect()
    cursor = cnx.cursor()
    cursor.execute("select r.id, r.company_name, r.url, r.topic from rss r, user_rss  ur where ur.id_user = '%d' and ur.id_rss = r.id" % (id))
    results = cursor.fetchall()
    rss = {}
    data = {}
    datos = []
    for row in results:
        data['id'] = row[0]
        data['company_name'] = row[1]
        data['url'] = row[2]
        data['topic'] = row[3]
        datos.append(data)
        data = {}
    rss['rss'] = datos
    cnx.close()
    return json.dumps(rss)


def getusersrssbytopic(id, topic):
    cnx = connect()
    cursor = cnx.cursor()
    cursor.execute("select r.id, r.company_name, r.url, r.topic from rss r, user_rss  ur where ur.id_user = '%d' and ur.id_rss = r.id and r.topic='%s'" % (id, topic))
    results = cursor.fetchall()
    rss = {}
    data = {}
    datos = []
    for row in results:
        data['id'] = row[0]
        data['company_name'] = row[1]
        data['url'] = row[2]
        data['topic'] = row[3]
        datos.append(data)
        data = {}
    rss['rss'] = datos
    cnx.close()
    return json.dumps(rss)


def setuser_rss(id_user, id_rss):
    cnx = connect()
    cursor = cnx.cursor()
    try:
        cursor.execute("INSERT INTO user_rss (id_user, id_rss) VALUES ('%d','%d')" % (id_user, id_rss))
        cnx.commit()
    except:
        cnx.rollback()
        return False
    return True
    cnx.close


def setuser(name, surname, nick, password, mail):
    cnx = connect()
    cursor = cnx.cursor()
    try:
        cursor.execute("INSERT INTO users (name, surname, nickname, pass, email) VALUES ('%s','%s','%s','%s','%s')" % (name, surname, nick, password, mail))
        cnx.commit()
    except:
        cnx.rollback()
        return False
    return True
    cnx.close


def checkuser(nick, password):
    cnx = connect()
    cursor = cnx.cursor()
    cursor.execute("SELECT * FROM users WHERE nickname = '%s' and pass = '%s'" % (nick, password))
    row = cursor.fetchone()
    if row != None:
        return True
    else:
        return False
    cnx.close


def getuserid(nickname):
    cnx = connect()
    cursor = cnx.cursor()
    cursor.execute("SELECT * FROM users WHERE nickname = '%s'" % (nickname))
    row = cursor.fetchone()
    if row != None:
        return row[0]
    else:
        return -1
    cnx.close


def removeuserrss(id_user, id_rss):
    cnx = connect()
    cursor = cnx.cursor()
    try:
        cursor.execute("DELETE FROM user_rss WHERE id_rss = '%d' and id_user = '%d'" % (id_rss, id_user))
        cnx.commit()
    except:
        cnx.rollback()
        return False
    return True
    cnx.close


def connect():
    return mysql.connect('127.0.0.1', 'root', 'pamaloyo18', 'rssandroid', port=3306)


if __name__ == '__main__':
    app.run(host='0.0.0.0')