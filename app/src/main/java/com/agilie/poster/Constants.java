package com.agilie.poster;

import com.agilie.camera.AspectRatio;

public interface Constants {

    AspectRatio DEFAULT_ASPECT_RATIO = AspectRatio.of(4, 3);
    AspectRatio ASPECT_RATIO_16_9 = AspectRatio.of(16, 9);

    String PHOTO_FOLDER = "/poster";
    String PHOTO_FORMAT = "%d.jpg";
}
