# HyperChat

A native android instant messaging app that aim to be combine all the features of popular instant messaging apps.

## Structure - Android JetPack
 - One main activity
 - All screens are fragments or multiple fragments
 - The data being displayed in the fragments are gotten through live data through ViewModels bound to the main activity, which all fragments has access to
 - There is a ViewModel for each screen (so the different content required for each different screen, will be handled by it's own separate ViewModel)
 - For accessing external data, there will be created repositories for each different data service i.e. a repository for Firebase and maybe another repository, if another external data source is needed. 

## Prerequisites
 - In order to be able to use the sign in with Google feature, you will need to use an emulator/physical phone with Google Play enabled, as this feature uses Google Play to sign you in with Google.

## Getting started


