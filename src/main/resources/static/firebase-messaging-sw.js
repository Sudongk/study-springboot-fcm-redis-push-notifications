importScripts('https://www.gstatic.com/firebasejs/5.9.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/5.9.2/firebase-messaging.js');

// Initialize Firebase
const firebaseConfig = {
    apiKey: "AIzaSyBQX72YAlh8Uqt636pPo7FhHcl5d7gA9Cw",
    authDomain: "board-api-e5a2c.firebaseapp.com",
    projectId: "board-api-e5a2c",
    storageBucket: "board-api-e5a2c.appspot.com",
    messagingSenderId: "1020922379294",
    appId: "1:1020922379294:web:34bdefab253778b376420f",
    measurementId: "G-2PE8SKLBE1"
};

firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();