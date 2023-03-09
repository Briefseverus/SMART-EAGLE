from time import sleep
import RPi.GPIO as GPIO
import picamera
import threading
import datetime
import pyrebase

firebase_config = {
    'apiKey': "AIzaSyBKlQEY2Fh-iUHi2lvwSov9B_xVEzq3Kss",
    'authDomain': "smarteagle-a3f43.firebaseapp.com",
    'projectId': "smarteagle-a3f43",
    'databaseURL':"https://smarteagle-a3f43-default-rtdb.firebaseio.com/",
    'storageBucket': "smarteagle-a3f43.appspot.com",
    'messagingSenderId': "147915554980",
    'appId': "1:147915554980:web:32c8c327d1718a78012631",
    'measurementId': "G-WNM2F3VHQW"
    }

firebase = pyrebase.initialize_app(firebase_config)

storage = firebase.storage()

db = firebase.database()

sensor_status = db.child("motion_sensors").get().val()

sensor_pin = 10
buzzer_pin = 8

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(sensor_pin, GPIO.IN)
GPIO.setup(buzzer_pin, GPIO.OUT)

camera = picamera.PiCamera()
camera.resolution = (2592,1944)

def buzzer_on():
    global sensor_status
    while True:
        if sensor_status == False:
            GPIO.output(buzzer_pin, False)
            continue
        
        if GPIO.input(10):
            while sensor_status:
                GPIO.output(buzzer_pin, True)
                sleep(0.5)
                GPIO.output(buzzer_pin, False)
                sleep(0.5)

def run_sensor():
    global sensor_status
    while True:
        if sensor_status == False:
            continue
        
        if GPIO.input(10):
            time = str(datetime.datetime.now())
            name = f'/home/pi/Pictures/{time}.jpg'
            camera.capture(name)
            storage.child(name).put(name)
            db.child("notifications").push({
            "title": "Motion Detected!",
            "body": "Someone has been detected on the camera!",
            "image": f"https://firebasestorage.googleapis.com/v0/b/xenon-container-239603.appspot.com/o/{name}?alt=media",
            "time": time
            })
            

if __name__ == '__main__':
    buzzer_thread = threading.Thread(target = buzzer_on)
    buzzer_thread.start()
    sensor_thread = threading.Thread(target = run_sensor)
    sensor_thread.start()
    print('--Program Started--')
    while True:
        sensor_status = db.child("motion_sensors").get().val()