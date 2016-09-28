# TaskMaster

## A simple to-do list manager for Android

Allows the user to manage tasks on a single mobile device. It supports adding, editing, and deleting tasks, as well as task persistence on the device.

This was developed By [George Cohn](https://github.com/GeoCohn) as an entry requirement for the [Codepath Android Mobile Bootcamp](https://codepath.com/androidbootcamp).
The buildup took about 16 hours.

### Prerequisities

You can build the app and sideload it onto any Android device or Android emulator running 4.0.3 or later.

### Installing

After cloning the repository, build and install using [this guideline](http://developer.android.com/tools/building/building-cmdline.html)

## User Stories

* From the main screen, use the fab (floating action button) to add new tasks.
* edit a task by clicking on it, and even add new tasks while editing by using the fab.
* Return to the main screen by using the back button. Use the save button first or changes will be lost.
* Mark a task as done from the main or detail screens
* Delete tasks by swiping them off the main screen.

### Additional Features
* Persistence of tasks is implemented in Sqlite via a Content Provider
* The main list is implemented as a RecyclerView
* The list is sorted by completion date, set in the detail view using a DatePicker
* The detail view is implemented in a DialogFragment, used as a dialog for new tasks, and as a regular fragment for editing.
* On larger wide devices the selected task's edit screen appears on the same page, alongside the task list.
* Task priorities (low, normal, urgent) are selectable in the Detail view via a spinner, and are visible as the task's background color on the main screen.
## Authors

* **George Cohn** - *Initial work* - https://github.com/GeoCohn

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
