/*
 * Copyright (c) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mythtv.leanfront.model;

import android.media.MediaDescription;
import android.os.Parcel;
import android.os.Parcelable;

import org.mythtv.leanfront.ui.MainFragment;

/**
 * Video is an object that holds the various metadata associated with a single video.
 */
public final class Video implements Parcelable, ListItem {
    public final long id;
    public final String title;
    public final String subtitle;
    public final String description;
    public final String bgImageUrl;
    public final String cardImageUrl;
    public final String videoUrl;
    public final String channel;
    public final String recordedid;
    public String recGroup;
    public int type;
    public final String season;
    public final String episode;
    // Format yyyy-mm-dd
    public final String airdate;
    // Format yyyy-mm-ddThh:mm:ssZ
    public final String starttime;
    public final String duration;
    public final String prodyear;
    public String progflags;
    // From MythTV libmyth/programtypes.h
    public static final int FL_WATCHED = 0x00000200;

    private Video(
            final long id,
            final String title,
            final String subtitle,
            final String desc,
            final String videoUrl,
            final String bgImageUrl,
            final String cardImageUrl,
            final String channel,
            final String recordedid,
            final String recGroup,
            final String season,
            final String episode,
            final String airdate,
            final String starttime,
            final String duration,
            final String prodyear,
            final String progflags) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.description = desc;
        this.videoUrl = videoUrl;
        this.bgImageUrl = bgImageUrl;
        this.cardImageUrl = cardImageUrl;
        this.channel = channel;
        this.recordedid = recordedid;
        this.recGroup = recGroup;
        this.season = season;
        this.episode = episode;
        this.airdate = airdate;
        this.starttime = starttime;
        this.duration = duration;
        this.prodyear = prodyear;
        this.progflags = progflags;
    }

    protected Video(Parcel in) {
        id = in.readLong();
        title = in.readString();
        subtitle = in.readString();
        description = in.readString();
        bgImageUrl = in.readString();
        cardImageUrl = in.readString();
        videoUrl = in.readString();
        channel = in.readString();
        recordedid = in.readString();
        recGroup = in.readString();
        season = in.readString();
        episode = in.readString();
        airdate = in.readString();
        starttime = in.readString();
        duration = in.readString();
        prodyear = in.readString();
        progflags = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    @Override
    public boolean equals(Object m) {
        return m instanceof Video && id == ((Video) m).id;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(description);
        dest.writeString(bgImageUrl);
        dest.writeString(cardImageUrl);
        dest.writeString(videoUrl);
        dest.writeString(channel);
        dest.writeString(recordedid);
        dest.writeString(recGroup);
        dest.writeString(season);
        dest.writeString(episode);
        dest.writeString(airdate);
        dest.writeString(starttime);
        dest.writeString(duration);
        dest.writeString(prodyear);
        dest.writeString(progflags);
    }

    @Override
    public String toString() {
        String s = "Video{";
        s += "id=" + id;
        s += ", recGroup='" + recGroup + "'";
        s += ", title='" + title + "'";
        s += ", subtitle='" + subtitle + "'";
        s += ", videoUrl='" + videoUrl + "'";
        s += ", bgImageUrl='" + bgImageUrl + "'";
        s += ", cardImageUrl='" + cardImageUrl + "'";
        s += ", recordedid='" + recordedid + "'";
        s += "}";
        return s;
    }

    @Override
    public int getItemType() {
        if (type != 0)
            return type;
        return recGroup == null || "".equals(recGroup)
                ? MainFragment.TYPE_VIDEO : MainFragment.TYPE_EPISODE;
    }

    @Override
    public String getName() {
        return videoUrl;
    }

    // Builder for Video object.
    public static class VideoBuilder {
        private long id;
        private String title;
        private String subtitle;
        private String desc;
        private String bgImageUrl;
        private String cardImageUrl;
        private String videoUrl;
        private String channel;
        private String recordedid;
        private String recGroup;
        private String season;
        private String episode;
        private String airdate;
        private String starttime;
        private String duration;
        private String prodyear;
        private String progflags;

        public VideoBuilder id(long id) {
            this.id = id;
            return this;
        }

        public VideoBuilder title(String title) {
            this.title = title;
            return this;
        }

        public VideoBuilder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public VideoBuilder description(String desc) {
            this.desc = desc;
            return this;
        }

        public VideoBuilder videoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
            return this;
        }

        public VideoBuilder bgImageUrl(String bgImageUrl) {
            this.bgImageUrl = bgImageUrl;
            return this;
        }

        public VideoBuilder cardImageUrl(String cardImageUrl) {
            this.cardImageUrl = cardImageUrl;
            return this;
        }

        public VideoBuilder channel(String channel) {
            this.channel = channel;
            return this;
        }

        public VideoBuilder recordedid(String recordedid) {
            this.recordedid = recordedid;
            return this;
        }

        public VideoBuilder recGroup(String recGroup) {
            this.recGroup = recGroup;
            return this;
        }


        public VideoBuilder season(String season) {
            this.season = season;
            return this;
        }

        public VideoBuilder episode(String episode) {
            this.episode = episode;
            return this;
        }

        public VideoBuilder airdate(String airdate) {
            this.airdate = airdate;
            return this;
        }

        public VideoBuilder starttime(String starttime) {
            this.starttime = starttime;
            return this;
        }

        public VideoBuilder duration(String duration) {
            this.duration = duration;
            return this;
        }

        public VideoBuilder prodyear(String prodyear) {
            this.prodyear = prodyear;
            return this;
        }

        public VideoBuilder progflags(String progflags) {
            this.progflags = progflags;
            return this;
        }

        public Video buildFromMediaDesc(MediaDescription desc) {
            return new Video(
                    Long.parseLong(desc.getMediaId()),
                    String.valueOf(desc.getTitle()),
                    "",
                    String.valueOf(desc.getDescription()),
                    "", // Media URI - not provided by MediaDescription.
                    "", // Background Image URI - not provided by MediaDescription.
                    String.valueOf(desc.getIconUri()),
                    String.valueOf(desc.getSubtitle()),
                    "", //recordid not provided
                    "","","","","","","",""
            );
        }

        public Video build() {
            return new Video(
                    id,
                    title,
                    subtitle,
                    desc,
                    videoUrl,
                    bgImageUrl,
                    cardImageUrl,
                    channel,
                    recordedid,
                    recGroup,
                     season,
                     episode,
                     airdate,
                     starttime,
                     duration,
                     prodyear,
                     progflags
            );
        }
    }
}
