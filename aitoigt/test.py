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
last_matrix = np.eye(4)

while True:

    if not server.is_connected():
        # Wait for client to connect
        sleep(0.1)
        continue

    # Init vars
    timestep += 1
    theta = timestep * 0.01

    # Generate transform
    matrix = np.eye(4)

    # Set position
    matrix[0, 3] = sin(theta) * 100.0
    matrix[1, 3] = sin(theta) * cos(theta) * 100.0
    
    # Set orientation
    direction = last_matrix[:3, 3] - matrix[:3, 3]
    angle = (-np.pi) + np.arctan2(direction[1], direction[0])   # -90 degree offset because -X is the forward direction of the IGTP pointer model
    nlah = np.array([[np.cos(angle), -np.sin(angle), 0], [np.sin(angle), np.cos(angle), 0], [0, 0, 1]])
    matrix[:3, :3] = nlah
    
    # Debugging
    print(f"Y Coord: {direction[1]}")
    print(f"X Coord: {direction[0]}")
    print(f"Angle: {angle}")

    # Send transform message
    last_matrix = matrix
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