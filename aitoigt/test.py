"""
============================
Tracked image data server
============================

Simple application that starts a server that sends images, transforms, and strings

"""

import pyigtl  # pylint: disable=import-error
from math import cos, sin, pi
from time import sleep
import numpy as np

server = pyigtl.OpenIGTLinkServer(port=18944, local_server=True)

image_size = [400, 200]

timestep = 0

while True:

    if not server.is_connected():
        # Wait for client to connect
        sleep(0.1)
        continue

    timestep += 1

    # Generate transform
    matrix = np.eye(4)
    matrix[0, 3] = sin(timestep * 0.01) * 20.0
    rotation_angle_rad = timestep * 0.5 * pi / 180.0
    matrix[1, 1] = cos(rotation_angle_rad)
    matrix[2, 1] = -sin(rotation_angle_rad)
    matrix[1, 2] = sin(rotation_angle_rad)
    matrix[2, 2] = cos(rotation_angle_rad)
    transform_message = pyigtl.TransformMessage(matrix, device_name="ImageToReference", timestamp=1)


    # Send messages
    server.send_message(transform_message)

    # Print received messages
    messages = server.get_latest_messages()
    for message in messages:
        print(message.device_name)

    # Do not flood the message queue,
    # but allow a little time for background network transfer
    sleep(0.01)