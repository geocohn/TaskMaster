# Pre-work - TaskMaster

## A simple to-do list manager for Android

TaskMaster is an android app that allows building a todo list and basic todo items management functionality including adding new items, editing and deleting an existing item.

Submitted by: [George Cohn](https://github.com/GeoCohn)

Time spent: 20 hours spent in total

## User Stories

The following required functionality is completed:

* [x] User can successfully add and remove items from the todo list
* [x] User can tap a todo item in the list and bring up an edit screen for the todo item and then have any changes to the text reflected in the todo list.
* [x] User can persist todo items and retrieve them properly on app restart

The following optional features are implemented:

* [x] Persist the todo items into SQLite instead of a text file
* [x] Improve style of the todo items in the list using a custom adapter
* [x] Add support for completion due dates for todo items (and display within listview item)
* [x] Use a DialogFragment instead of new Activity for editing items
* [x] Add support for selecting the priority of each todo item (and display in listview item)
* [x] Tweak the style improving the UI / UX, play with colors, images or backgrounds

The following additional features are implemented:

* [x] A fab (floating action button) is used to add new tasks
* [x] Add new tasks even while editing an existing one, by using the fab
* [x] Mark a task as done from the main or detail screens
* [x] Delete tasks by swiping them off the main screen
* [x] Persistence of tasks is implemented in Sqlite via a Content Provider
* [x] The main list is implemented as a RecyclerView
* [x] The list is sorted by completion date, set in the detail view using a DatePicker
* [x] The DialogFragment is used both as a dialog for new tasks, and as a regular fragment for editing
* [x] The detail screen uses a ConstraintLayout
* [x] On larger wide devices the selected task's edit screen appears on the same page, alongside the task list
* [x] Task priorities (low, normal, urgent) visible as the task's background tint in the main view

## Video Walkthrough

Here's a walkthrough of implemented user stories:

![Video walkthrough](./screen%20captures/device-2016-09-27-163004.gif?raw=true)

GIF created on Ubuntu 16.04 using ffmpeg and convert.

## Notes

The framework is based on a Master/Detail flow template generated in Android Studio. It was then modified to access a Content Provider via a Cursor Loader, in order to automatically deal with synchronization/thread issues.

Several TODO items come immediately to mind

* Dates should be managed internally as Java Dates, and converted to strings only for display and serialization/deserialization
* The date display format should be locale specific, or conform to user preferences
* The Save button is awkward, and may even disappear in the New Task dialog if the Task Description grows too large
* The List and Detail code have some duplicated code that should be refactored as static methods in some Util type class

## License

Copyright 2016 George Cohn III

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
