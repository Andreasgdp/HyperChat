# HyperChat

<p align="center">
  <img width="460" height="150" src="https://user-images.githubusercontent.com/39928082/164708009-1a65d641-ac4d-46b2-981a-c0f7f723c9d0.png">
</p>

A native android instant messaging app that aim to be combine all the features of popular instant messaging apps.

## Structure - Android JetPack (almost)

-   One main activity
-   All screens are fragments or multiple fragments
-   The data being displayed in the fragments are gotten through live data through ViewModels bound to the fragments.
-   There is a ViewModel for each screen (so the different content required for each different screen, will be handled by it's own separate ViewModel as far as the logic for the screens differ)
-   For accessing external data, there will be created repositories for each different data service i.e. a repository for Firebase and maybe another repository, if another external data source is needed. (This is done to make the app more flexible and allow for the addition of new data sources). Currently, there is only one repository, but it can be extended to include more repositories.

## Prerequisites

-   In order to be able to use the sign in with Google feature, you will need to use an emulator/physical phone with Google Play enabled, as this feature uses Google Play to sign you in with Google.
    -   In order for google sign in you will also need to create an SHA-1 key from the repository, which needs to be added to firebase.

## Getting started

-   To get started, you will need to clone the repository and then run the gradle build command.
-   Then you should be able to run the app on your emulator/physical phone.

## Features

-   The app features a native android instant messaging app, which is a combination of the features of the popular instant messaging apps. Features such as

### Authentication system

-   The app has a Firebase authentication system, which is used to authenticate users.
-   The app has a Google sign in feature, which is used to sign in users with Google.

### Messaging system

-   The app has a Firebase messaging system, which is used to send and receive messages.
-   The messaging system can also handle deletion of messages by long pressing on them.

### Personal information system

-   The app has a Firebase database system, which is used to store and retrieve personal information.
-   This information is used to display the user's profile picture, name, and about information in the app.

### Dark mode
-  The app has a dark mode feature, which is used to change the color of the app to a dark mode. This is activated by the native android dark mode feature.

## Showcase
https://user-images.githubusercontent.com/39928082/164706995-f463abdf-afec-4900-bd8f-b8485f1dcd96.mp4

