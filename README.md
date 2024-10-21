"Media Store App"
Media Store App is an Android application that displays and manages various types of media stored on the device, including images, videos, audio files, documents, contacts, and storage information. The app consolidates all media types into a single interface, allowing users to view, organize, and interact with their media content.

"Features"
Storage Information: Displays the device's total, used, and available storage.
Images: View and browse all images stored on the device.
Videos: Play and browse video files. A small video icon overlay is shown on thumbnails for easy identification.
Audio: Displays a list of audio files with their name and size.
Documents: Lists all document files (e.g., PDFs, text files) with their names and sizes.
Contacts: Displays saved contacts with names and phone numbers.
Media Preview: Uses Glide for loading and previewing images and videos efficiently.

"Technologies and Libraries"
Android SDK
ViewBinding for UI interaction
RecyclerView for media display
Glide for efficient media loading and caching
MediaStore API for retrieving media content from the device
Dependency Injection (Hilt) for dependency management
Permission Handling for accessing media and storage

"App Structure"
The application is structured as follows:

Adapters: One adapter to handle different media types (images, videos, contacts, audio, and documents) using a RecyclerView.
MediaStore API: Used to access the device's media files and content providers (e.g., contacts).
Media Types: Includes IMAGE, VIDEO, CONTACT, AUDIO, and DOCUMENT. Each media type is loaded into specific layouts.
UI Layouts: The app features custom layouts for each media type, with video files showing an overlay icon to indicate they are playable.
