
# Reading Sms from Another device

This project uses two app that first is Sms sender 
second is receiver app.



## Demo
![]([Sms_Reader/sender.gif](https://github.com/MuhammadXr/Sms_Reader/blob/master/sender.gif))
[receiver.gif]
## Appendix

1. First app will choose avaible sim card in android device. It will take sms and will send through FireBaseCloudMessaging (FCM) to subscribed devices.

2. Second App will be receiver. It will be subscribed when it opened


## Sender App Features

- Choosing avaible simcard in device
- Turning off battery optimizations
- Working in foreground
- Autostart after reboot
- Sending FCM messages without having cloud server

## Receiver App Features

- Subscribes to FCM channel
- Triggering Notification
## Authors

- [@MuhammadXR](https://github.com/MuhammadXr)

