# MythTV leanfront: Android TV frontend for MythTV

This is based on a clone of the sample Videos By Google app, designed to run on an Android TV device (such as the Shield or Amazon Fire Stick). It uses the Leanback Support library which enables you to easily develop beautiful Android TV apps with a user-friendly UI that complies with the UX guidelines of Android TV.

## Features

- 4K video plays at 60fps with full 4K resolution. This is currently not achievable with the android port of mythfrontend.
- The application uses exoplayer, which is the player code used by youtube, Amazon Prime and others. As such it will be able to handle new capabilities that are released on Android TV.
- It plays recordings and videos from a MythTV backend. All recordings are presented in a way that is consistent with other leanback applications. The first screen shows a list of recording group. You can drill down to a list of titles in a recording group.
- This application uses the MythTV api to communicate with the backend. It needs no access to the database password, and will work on all versions of mythbackend from v29 onwards. It may work on older versions if the apis are available on the MythTV backend.
- Voice search within the application is supported.
- With backend on master or recent MythTV V30 this frontend will prevent idle shutdown on the backend. On older backends you need to take steps to ensure the backend does not shut down while playback is occurring.
- Bookmarks are supported. Bookmarks can be stored on MythTV (for recordings) or on the local leanback frontend (for recordings or videos). In cases where there is no seektable the system stores the bookmark on MythTV based on an assumed frame rate. The frame rate can be set in the Settings page. If the frame rate set is different from the actual frame rate, the location of the bookmark set here will be incorrect when viewed from mythfrontend. Not thet video bookmarks will always be stored locally.
- The "Watched" flag is set if you get to the end of the recording during playback. To ensure it is set, press forward to get to the end before exiting playback.
- There is a delete/undelete option so that you can delete shows after watching. Also set watched or unwatched and remove bookmark options.
- There is a zoom icon and an aspect icon so that you can expand letterbox recordings and correct wrongly stretched recordings.
- There is an icon to pin the enlargement to the top, middle or bottom. If you want to hide a ticker at the bottom, you can pin to the top then enlarge, which will leave the top in place and enlarge downwards so that the ticker is off screen.
- Videos do not currently support deletion or bookmarks stored on MythTV. Bookmarks for videos are stored locally on the android tv device.
- Wakeup of master backend is supported via setup.
- Sort order of recordings can be customized.
- Subtitles (Closed captions) are supported.
- At the end of a recording playback, you can advance to the next episode or any episode without returning to the main list.
- You can play in-progress recordings and the application will follow the progress as the recording continues. This way you almost have LiveTV support, if you first start a recording or a LiveTV session via mythfrontend, and then refresh the list in leanfront.
- Video playback is exlusively via hardware assisted mediacodec.
- Audio playback is supported using mediacodec (hardware) or ffmpeg (software). By default it will use mediacodec if it can, and will switch to ffmpeg if there is a media format not supported by mediacodec. There is a setting where you can change this default and force either mediacodec or ffmpeg.
- Audio playback supports digital passthrough for AC3 and other digital formats if they are supported on your sound system. It also supports downmix to stereo if you do not have a system that supports AC3.
- Selection of alternate audio tracks during playback.
- Playback from slave backends is now supported.

## Main Screen

- A list of recording groups is displayed on the left with titles in the group in a scrolling row on the right. Select a group and press enter to open the screen with that group's contents.
- There is a row for "All" at the top.
- After the recording groups there is a row for "Videos", which shows the MythTV Videos by directory.
- There is a row labeled "Settings" at the bottom. select "Settings" and press enter to see and update program settings.
- There is a "Refresh" icon on the settings row to refresh the list of recordings and videos from the backend. Note that the list is also refreshed after using Settings. The refresh does not perform a rescan at the backend, currently you will have to do it from a normal frontend or run "mythutil --scanvideos" on the backend.

## Playback

- Pressing Enter, up or down brings up the OSD playback controls. Note if you have enabled up/down jumping then up and down will cause a jump instead.
- Left and right arrow will skip back and forward. Holding down the arrow moves quickly through the video. The number of seconds for forward and back skip are customizable in Settings.
- Up and down arrow can be used for bigger jumps by setting a jump interval in settings. I recommend against using this because it interferes with navigation in the OSD. You can move very quickly through playback by holding down left or right arrow`, so jump is not really needed. Jumping can be disabled by setting blank or 0 in the jump interval in Settings. When jumping with up and down arrows, the arrow buttons are disabled for up/down use in the OSD, and this can cause confusion.
- If you are playing a recording that is in progress of being recorded, the behavior will be as follows. When you start watching, the OSD will show the duration being as much as has been recorded at that time. This duration will remain at that figure as you continue watching. Once you get to that point in the recording, there is a slight pause, then playback continues, with duration shown as "---", which means unknown duration. While in this state, if you press froward or back skip, it will revert to showing the amount recorded to date, and perform the forward or back skip requested. When you eventually get to the end as it was when you did the skip operation, it will revert to duration showing as "---" while playback continues.

## Problems

Some recordings or videos may not play correctly, or may not play at all. In some cases,
videos may play but the duration may not show in the OSD and skipping forward may not work.
In some cases audio may be garbled. We are looking into these problems and hope to have a
solution. If the recording plays correctly with mythfrontend or VLC but not with leanfront,
try running this command against the file. Use this for recorded programs only (mpeg ts streams):

```
ffmpeg -i inputfile -acodec copy -vcodec copy -scodec copy -f mpegts outputfile
```
You can overwrite the recording file with the output from this command. This may
repair the file so that it works correctly.

## Playback controls (OSD)

![](PlaybackExample.png)

The following controls are available when pressing enter during playback. Select an icon and press enter to apply it.

### Top Row of Controls

| Icon | Usage |
|------|-------|
| Pause/Resume | Switches between pause icon and play icon depending on the current state. |
| Previous track | This plays, from the beginning, the previous recording or video in the list without needing to exit from playback (see related videos, below). |
| Rewind | This skips back by the time set in the settings. |
| Fast Forward | This skips forward by the time set in the settings. |
| Next track | This plays, from the beginning, the next recording or video in the list without needing to exit from playback (see related videos, below). |
| Slow down | Slows playback speed by increments down to a minimum of 50%. |
| Speed up | Speeds up playback by increments to a maximum of 800%. |

### Progress Bar

This shows playback position plus time played and total time. While this is focused you can use left and right arrows to skip back and forward. Holding the arrow down moves quickly through the recording. While this is focused, pressing Enter pauses and resumes.

### Bottom Row of controls

| Icon | Usage |
|------|-------|
| CC | Turns on or off captions (subtitles). If there are multiple languages this rotates among them. |
| Zoom | Changes the picture size. Pressing this rotates among several standard zoom amounts. |
| Aspect | Stretch or squeeze the picture in case it is showing at the wrong aspect ratio. Pressing this rotates between several common aspect ratios. |
| Up/down | If the picture has been resized, moves the picture up or down. There are three positions, aligned on top, middle, or bottom. For use when you want to cut off the top or bottom of the picture, after zooming to a bigger size. |
| Audio Track | Rotates among available audio tracks. |

**Note:** To use *slow down* or *speed up* you have to disable digital audio passthrough, by either selecting *Stereo* in Android settings or selecting *FFmpeg* in leanfront settings.

### Related videos

To see Related videos press down arrow. This shows other videos / recordings in the current group. You can select one of these to play from the beginning instead of the current playing video.

## Remote Control

The Fire Stick and NVidia Shield have rather limited remote controls, however there are ways of connecting more advanced remotes, and a TV remote can be used to control the Android TV device using CEC. MythTV leanfront supports many media control keys that could be available.

| Key | Context | Usage |
|-----|---------|-------|
| Back | Playback | If OSD is showing, close OSD. Otherwise Stop Playback and save bookmark |
| Captions | Playback | Rotates among available captions (same as CC icon) |
| DPad Left | Playback | Skip back number of seconds specified in settings (default is 20) |
| DPad Right | Playback | Skip forward number of seconds specified in settings (default is 60) |
| DPad Up | Playback | Show OSD and navigate in OSD |
| DPad Down | Playback | Show OSD and navigate in OSD |
| DPad Center | Playback | Show OSD |
| Media Audio Track | Playback | Rotate among available audio tracks (same as Ear icon) |
| Media Pause | Playback | Pause playback |
| Media Play | Playback | Resume playback if paused |
| Media Play Pause | Playback | Toggle playback between playing and paused |
| Media Fast Forward | Playback | Skip forward number of seconds specified in settings (default is 60) |
| Media Rewind | Playback | Skip back number of seconds specified in settings (default is 20) |
| Media Skip Forward | Playback | Jump forward number of minutes specified in settings (default is 5) |
| Media Skip Backward | Playback | Jump back number of minutes specified in settings (default is 5) |
| Media Stop | Playback | Stop Playback and save bookmark |
| Media Next | Playback | Skip to beginning of the next Video (same as Next Track Icon) |
| Media Previous | Playback | Skip to beginning of the previous Video (same as Previous Track Icon) |
| TV Zoom Mode | Playback | Squeeze or Stretch the picture (same as Aspect Icon) |
| Zoom In | Playback | Reduce the picture size (similar to Zoom Icon). |
| Zoom Out | Playback | Increase the picture size (similar to Zoom Icon) |
| Media Play | List | Play selected video from bookmark or beginning without first displaying details page |
| Media Play Pause | List | Play selected video from bookmark or beginning without first displaying details page |
| Media Play | Details Page | Play video from bookmark or beginning |
| Media Play Pause | Details Page | Play video from bookmark or beginning |

If "Use Up/Down Arrows for Jump" is selected in settings, the following apply. However, this may make navigating the OSD more difficult.

| Key | Context | Usage |
|-----|---------|-------|
| DPad Up | Playback | Jump forward number of minutes specified in settings (default is 5) |
| DPad Down | Playback | Jump back number of minutes specified in settings (default is 5) |

## Android Phones / Tablets

It is not recommended to run leanfront on an Android phone or tablet. You can install it if
you are running Android 5.0 (Lollipop) or later version, but controlling it through the touch
screen does not work correctly.
The application reacts to the touch screen, but it is not usable.
It may be possible to run it on a phone or tablet if you attach a remote control
or a keyboard.

## Leanfront Restrictions / Limitations

These may be addressed in a future release.

- There is no support for watching LiveTV at present.
- The *Master Backend Override* setting does not work. It is ignored.
- There is no support at present for showing program listings or scheduling recordings.
- Moving recordings to new recording groups is not supported.
- Metadata input and update are not supported.
- Request of video file scan is not supported.

## Download and install

- Download the latest apk from  [Bintray][bintray].
- Enable developer mode on your android device.
- install adb on your computer
- Run these

```
    adb connect <android-ip-address>
    adb install -r <apk-name>
```
Alternatively, if you have a browser on your android device you can avoid using developer mode.

- Enable installation of apps from unknown source in Android settings.
- Navigate to the download site (https://dl.bintray.com/bennettpeter/generic/mythtv_leanfront/android), select the latest version, tap it and request the system to install it.

## To Do List

Possible additions.

Further development will continue. These are some possible additions.

- Automatically switch to ffmpeg decoding if you use speedup, instead of prompting you to change your settings.
- Allow search from android home screen.
- Allow recommendations from android home screen.
- Amazon specific search and recommendations.
- Program guide.
- Live TV.
- Create recording rules.

The following items will need api changes on the backend

- Video scan
- Video delete
- Video bookmarks stored on the backend.
- Change recording group on a recording.

## Building

- Download and install [Android Studio][studio]. Also download the latest ndk and Cmake from within android studio.
- In the $HOME/Android directory create a link to the ndk, for example android-ndk -> Sdk/ndk/21.0.6113669
- In the app/src/main/jni/ directory, run download_ffmpeg.sh and build_ffmpeg.sh.
- Open the project in [Android Studio][studio].
- Compile and deploy to your Android TV device (such as a Shield or Amazon fire stick). 
- It can also be run with an android emulator, but the emulator that comes with android studio does not support MPEG2 playback, so you need to play an h264 or h265 recording.
- If you do not want to build this yourself, there is a package at [Bintray][bintray].

## Running

Start up the app. There is an entry on the main screen at the end called "settings". There you need to enter the backend ip address. There are other options available here.

If using backend earlier than fixes/30 of Nov 12 2019 or Master of October 31 2019, make sure the backend is not set up for automatic shutdown when inactive. Otherwise it may shut down during playback.

## License

Licensed under the GNU GPL version 3 or later. See the [LICENSE file][license] for details.

[studio]: https://developer.android.com/tools/studio/index.html
[license]: LICENSE
[bintray]: https://dl.bintray.com/bennettpeter/generic/mythtv_leanfront/android
