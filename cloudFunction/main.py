from google.cloud import firestore
import os
import json
import populartimes

client = firestore.Client()
MAPSKEY = os.environ.get('mapskey', '')

# # Inspired by https://cloud.google.com/functions/docs/calling/cloud-firestore#functions_firebase_firestore-python


# def on_place_create(data, context):
#     path_parts = context.resource.split('/documents/')[1].split('/')
#     collection_path = path_parts[0]
#     document_path = '/'.join(path_parts[1:])

#     affected_doc = client.collection(collection_path).document(document_path)

#     place_id = data["value"]["fields"]["placeId"]["stringValue"]

#     print(f"Attempting to find popular times for {place_id}")
#     populartimes_data = populartimes.get_id(MAPSKEY, place_id) or {}
#     print(f"Received the following data {populartimes_data}")
#     affected_doc.update({
#         u'busyTimes': populartimes_data.get("populartimes"),
#         u'waitTimes': populartimes_data.get("time_wait"),
#         u'avgSpentTimes': populartimes_data.get("time_spent"),
#     })

def on_place_create(request):
    request_args = request.args

    if request_args and 'placeId' in request_args:
        populartimes_data = populartimes.get_id(
            MAPSKEY, request_args["placeId"]) or {}
        response = {
            'busyTimes': populartimes_data.get("populartimes"),
            'waitTimes': populartimes_data.get("time_wait"),
            'avgSpentTimes': populartimes_data.get("time_spent"),
        }
    else:
        response = {
            "error": "No place id provided"
        }
    return json.dumps(response)
