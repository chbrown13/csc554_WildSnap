package com.samsung.android.sdk.camera.sample.cases.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.camera2.params.TonemapCurve;
import android.os.Build;
import android.util.Range;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.samsung.android.sdk.camera.SCameraCharacteristics;
import com.samsung.android.sdk.camera.SCaptureRequest;
import com.samsung.android.sdk.camera.SCaptureResult;
import com.samsung.android.sdk.camera.sample.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Setting dialog class for sample single.
 * Controls setting UI and dependency between SCaptureRequest.Keys.
 * Update SCaptureRequest.Builder as setting UI changes.
 */
public class SettingDialog implements SettingItem.OnSettingItemValueChangedListener {

    private final Activity mActivity;

    /**
     * Instance of dialog
     */
    private AlertDialog  mDialog;

    /**
     * Array holds an instance of {@link com.samsung.android.sdk.camera.SCaptureRequest.Builder}
     */
    private SCaptureRequest.Builder[] mBuilders;

    /**
     * Map holds original valued of dimmed setting item
     */
    private Map<SCaptureRequest.Key<?>, Object> mDimMap;

    /**
     * Map holds SettingItem for each setting key
     */
    private Map<Object, SettingItem<?, ?>> mSettingItemMap;

    /**
     * Instance of camera setting update event listener
     */
    private OnCameraSettingUpdatedListener mListener;

    /**
     * Indicate whether SettingDialog is configured.
     */
    private boolean mConfigured;

    /**
     * Default zoom value (1x)
     */
    private Rect mDefaultZoom;

    /**
     * Listener interface for setting update event
     */
    public interface OnCameraSettingUpdatedListener {
        public void onCameraSettingUpdated(int key, int value);
    }

    /**
     * Constructor
     */
    public SettingDialog(Activity activity) {
        mActivity = activity;

        mDimMap = new HashMap<SCaptureRequest.Key<?>, Object>();
        mSettingItemMap = new HashMap<Object, SettingItem<?, ?>>();
    }

    /**
     * Show setting dialog
     */
    public void show() {
        if(mDialog != null && !mDialog.isShowing()) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDialog.show();
                }
            });
        }
    }

    /**
     * Dismiss setting dialog
     */
    public void dismiss() {
        if(mDialog != null && mDialog.isShowing()) mDialog.dismiss();
    }

    /**
     * Set {@link SettingDialog.OnCameraSettingUpdatedListener}
     *
     * @param listener Instance of listener.
     */
    public void setOnCaptureRequestUpdatedListener(OnCameraSettingUpdatedListener listener) {
        mListener = listener;
    }

    /**
     * Initialize dialog. Add each setting items if device supports it.
     */
    private void initDialog(SCameraCharacteristics characteristics, List<Integer> availableLensFacing, List<Integer> availableImageFormat) {

        View dialogView = mActivity.getLayoutInflater().inflate(R.layout.setting_dialog_single, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, mActivity.getResources().getDisplayMetrics()));

        // Add Camera Facing option
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("Lens facing",
                        Integer.valueOf(SettingItem.SETTING_TYPE_CAMERA_FACING),
                        Arrays.asList("Rear", "Front"),
                        Arrays.asList(SCameraCharacteristics.LENS_FACING_BACK,
                                SCameraCharacteristics.LENS_FACING_FRONT),
                        availableLensFacing),
                layoutParams);

        // AF mode is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("AF mode",
                        SCaptureRequest.CONTROL_AF_MODE,
                        Arrays.asList("Auto", "Off", "Macro", "Continuous Video", "Continuous Picture", "EDOF"),
                        Arrays.asList(SCaptureRequest.CONTROL_AF_MODE_AUTO,
                                SCaptureRequest.CONTROL_AF_MODE_OFF,
                                SCaptureRequest.CONTROL_AF_MODE_MACRO,
                                SCaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO,
                                SCaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE,
                                SCaptureRequest.CONTROL_AF_MODE_EDOF),
                        asList(characteristics.get(SCameraCharacteristics.CONTROL_AF_AVAILABLE_MODES))),
                layoutParams);

        // Phase AF UI is added if device supports it.
        if(characteristics.getKeys().contains(SCameraCharacteristics.PHASE_AF_INFO_AVAILABLE) &&
                characteristics.get(SCameraCharacteristics.PHASE_AF_INFO_AVAILABLE))
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("Phase AF",
                            SCaptureRequest.PHASE_AF_MODE,
                            Arrays.asList("On", "Off"),
                            Arrays.asList(SCaptureResult.PHASE_AF_MODE_ON,
                                    SCaptureRequest.PHASE_AF_MODE_OFF),
                            Arrays.asList(SCaptureResult.PHASE_AF_MODE_ON,
                                    SCaptureRequest.PHASE_AF_MODE_OFF)),
                    layoutParams);
        else
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("Phase AF"), layoutParams);

        // Focus distance control is available if device reports MANUAL_SENSOR capability. Just double check from available capture request key list.
        if(asList(characteristics.get(SCameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)).contains(SCameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR)  &&
                characteristics.get(SCameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE) > 0.0f &&
                characteristics.getAvailableCaptureRequestKeys().contains(SCaptureRequest.LENS_FOCUS_DISTANCE))
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("Focus Distance",
                            SCaptureRequest.LENS_FOCUS_DISTANCE,
                            new Range<Float>(0.0f, characteristics.get(SCameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE))),
                    layoutParams);
        else
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("Focus Distance"), layoutParams);

        {
            // If device supports scene mode, it does not contains DISABLED in available scene mode list.
            // add it here to maintain consistency.
            List<Integer> availableValueList = asList(characteristics.get(SCameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES));
            if(!availableValueList.contains(SCaptureRequest.CONTROL_SCENE_MODE_DISABLED)) availableValueList.add(SCaptureRequest.CONTROL_SCENE_MODE_DISABLED);

            ((LinearLayout) dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("Scene mode",
                            SCaptureRequest.CONTROL_SCENE_MODE,
                            Arrays.asList("Off", "Face priority", "Action", "Portrait", "Landscape", "Night", "Night portrait", "Theatre", "Beach", "Snow", "Sunset", "Steady photo", "Fireworks", "Sports", "Party", "Candle light"),
                            Arrays.asList(SCaptureRequest.CONTROL_SCENE_MODE_DISABLED,
                                    SCaptureRequest.CONTROL_SCENE_MODE_FACE_PRIORITY,
                                    SCaptureRequest.CONTROL_SCENE_MODE_ACTION,
                                    SCaptureRequest.CONTROL_SCENE_MODE_PORTRAIT,
                                    SCaptureRequest.CONTROL_SCENE_MODE_LANDSCAPE,
                                    SCaptureRequest.CONTROL_SCENE_MODE_NIGHT,
                                    SCaptureRequest.CONTROL_SCENE_MODE_NIGHT_PORTRAIT,
                                    SCaptureRequest.CONTROL_SCENE_MODE_THEATRE,
                                    SCaptureRequest.CONTROL_SCENE_MODE_BEACH,
                                    SCaptureRequest.CONTROL_SCENE_MODE_SNOW,
                                    SCaptureRequest.CONTROL_SCENE_MODE_SUNSET,
                                    SCaptureRequest.CONTROL_SCENE_MODE_STEADYPHOTO,
                                    SCaptureRequest.CONTROL_SCENE_MODE_FIREWORKS,
                                    SCaptureRequest.CONTROL_SCENE_MODE_SPORTS,
                                    SCaptureRequest.CONTROL_SCENE_MODE_PARTY,
                                    SCaptureRequest.CONTROL_SCENE_MODE_CANDLELIGHT),
                            availableValueList),
                    layoutParams);
        }

        // Face detection mode is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("Face detect",
                        SCaptureRequest.STATISTICS_FACE_DETECT_MODE,
                        Arrays.asList("Off", "Simple", "Full"),
                        Arrays.asList(SCaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF,
                                SCaptureRequest.STATISTICS_FACE_DETECT_MODE_SIMPLE,
                                SCaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL),
                        asList(characteristics.get(SCameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES))),
                layoutParams);

        // AE mode is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("AE mode",
                        SCaptureRequest.CONTROL_AE_MODE,
                        Arrays.asList("On", "Off", "Auto flash", "Auto flash (red eye+)", "Always flash"),
                        Arrays.asList(SCaptureRequest.CONTROL_AE_MODE_ON,
                                SCaptureRequest.CONTROL_AE_MODE_OFF,
                                SCaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH,
                                SCaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE,
                                SCaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH),
                        asList(characteristics.get(SCameraCharacteristics.CONTROL_AE_AVAILABLE_MODES))),
                layoutParams);

        // AE lock is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("AE Lock",
                        SCaptureRequest.CONTROL_AE_LOCK,
                        Arrays.asList("Unlock", "Lock"),
                        Arrays.asList(false, true),
                        Arrays.asList(false, true)),
                layoutParams);

        // AE anti-banding is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("AE anti-banding",
                        SCaptureRequest.CONTROL_AE_ANTIBANDING_MODE,
                        Arrays.asList("Auto", "Off", "50Hz", "60Hz"),
                        Arrays.asList(SCaptureRequest.CONTROL_AE_ANTIBANDING_MODE_AUTO,
                                SCaptureRequest.CONTROL_AE_ANTIBANDING_MODE_OFF,
                                SCaptureRequest.CONTROL_AE_ANTIBANDING_MODE_50HZ,
                                SCaptureRequest.CONTROL_AE_ANTIBANDING_MODE_60HZ),
                        asList(characteristics.get(SCameraCharacteristics.CONTROL_AE_AVAILABLE_ANTIBANDING_MODES))),
                layoutParams);

        // AE compensation is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("AE Compensation",
                        SCaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION,
                        characteristics.get(SCameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)),
                layoutParams);

        // Target FPS range
        {
            List<String> fpsRangeStringList = new ArrayList<String>();
            List<Range<Integer>> fpsList = Arrays.asList(characteristics.get(SCameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES));
            Collections.reverse(fpsList);

            for(Range<Integer> fps : fpsList) fpsRangeStringList.add(fps.toString());

            ((LinearLayout) dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("Target FPS",
                            SCaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
                            fpsRangeStringList,
                            fpsList,
                            fpsList),
                    layoutParams);
        }

        // ISO control is available if device reports MANUAL_SENSOR capability. double check from available key list.
        if(asList(characteristics.get(SCameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)).contains(SCameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR) &&
                characteristics.getAvailableCaptureRequestKeys().contains(SCaptureRequest.SENSOR_SENSITIVITY)) {
            List<Integer> isoList = new ArrayList<Integer>();
            List<String> isoStringList = new ArrayList<String>();
            for(int iso : new int[]{50, 100, 200, 400, 800, 1600, 3200, 6400, 12800}) {
                if(characteristics.get(SCameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE).contains(iso)) {
                    isoList.add(iso);
                    isoStringList.add("" + iso);
                }
            }
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("ISO",
                            SCaptureRequest.SENSOR_SENSITIVITY,
                            isoStringList,
                            isoList,
                            isoList),
                    layoutParams);
        } else {
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("ISO"), layoutParams);
        }

        // Exposure time control is available if device reports MANUAL_SENSOR capability. double check from available key list.
        if(asList(characteristics.get(SCameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)).contains(SCameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR) &&
                characteristics.getAvailableCaptureRequestKeys().contains(SCaptureRequest.SENSOR_EXPOSURE_TIME)) {
            List<Long> shutterSpeedList = new ArrayList<Long>();
            List<String> shutterSpeedStringList = new ArrayList<String>();

            // denominator of the fractional shutter speed. For example, '10' indicates 1/10sec. Following routine will convert to nano sec. and added to list. only for that supported by the device.
            // SENSOR_INFO_EXPOSURE_TIME_RANGE can be configures unit of nano seconds. However, sample app will use list of widely accepted (by DSLR systems) shutter speeds.
            for(int speed : new int[]{2, 3, 4, 5, 6, 8, 10, 13, 15, 20, 25, 30, 40, 50, 60, 80, 100, 125, 160, 200, 250, 320, 400, 500, 640, 800, 1000, 1250, 1600, 2000, 2500, 3200, 4000, 5000, 6400, 8000}) {
                if(characteristics.get(SCameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE).contains(1000000000l / speed)) {
                    shutterSpeedList.add(1000000000l / speed);
                    shutterSpeedStringList.add("1/" + speed + "s");
                }
            }

            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("Shutter speed",
                            SCaptureRequest.SENSOR_EXPOSURE_TIME,
                            shutterSpeedStringList,
                            shutterSpeedList,
                            shutterSpeedList),
                    layoutParams);
        } else {
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("Shutter speed"), layoutParams);
        }

        // Metering mode UI is added if device supports it.
        if(characteristics.getKeys().contains(SCameraCharacteristics.METERING_AVAILABLE_MODES))
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("Metering mode",
                            SCaptureRequest.METERING_MODE,
                            Arrays.asList("Matrix", "Center", "Spot", "Manual"),
                            Arrays.asList(SCaptureRequest.METERING_MODE_MATRIX,
                                    SCaptureRequest.METERING_MODE_CENTER,
                                    SCaptureRequest.METERING_MODE_SPOT,
                                    SCaptureRequest.METERING_MODE_MANUAL),
                            asList(characteristics.get(SCameraCharacteristics.METERING_AVAILABLE_MODES))),
                    layoutParams);
        else
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("Metering mode"), layoutParams);

        // Live HDR is added if device supports it.
        if(characteristics.getKeys().contains(SCameraCharacteristics.LIVE_HDR_INFO_LEVEL_RANGE) &&
                characteristics.get(SCameraCharacteristics.LIVE_HDR_INFO_LEVEL_RANGE).getUpper() > characteristics.get(SCameraCharacteristics.LIVE_HDR_INFO_LEVEL_RANGE).getLower()) {
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("RT-HDR",
                            SCaptureRequest.CONTROL_LIVE_HDR_LEVEL,
                            characteristics.get(SCameraCharacteristics.LIVE_HDR_INFO_LEVEL_RANGE)),
                    layoutParams);
        } else {
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("RT-HDR"), layoutParams);
        }

        // Available OIS mode value can be null. Check here.
        if(characteristics.getKeys().contains(SCameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION) &&
                characteristics.getAvailableCaptureRequestKeys().contains(SCaptureRequest.LENS_OPTICAL_STABILIZATION_MODE))
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("Optical Image Stabilization",
                            SCaptureRequest.LENS_OPTICAL_STABILIZATION_MODE,
                            Arrays.asList("Off", "On"),
                            Arrays.asList(SCaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_OFF,
                                    SCaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON),
                            asList(characteristics.get(SCameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION))),
                    layoutParams);
        else
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("Optical Image Stabilization"), layoutParams);

        // OIS operation mode is added if device supports it.
        if(characteristics.getKeys().contains(SCameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION_OPERATION_MODE))
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("OIS Mode",
                            SCaptureRequest.LENS_OPTICAL_STABILIZATION_OPERATION_MODE,
                            Arrays.asList("Picture Mode", "Video Mode"),
                            Arrays.asList(SCaptureRequest.LENS_OPTICAL_STABILIZATION_OPERATION_MODE_PICTURE,
                                    SCaptureRequest.LENS_OPTICAL_STABILIZATION_OPERATION_MODE_VIDEO),
                            asList(characteristics.get(SCameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION_OPERATION_MODE))),
                    layoutParams);
        else {
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("OIS Mode"), layoutParams);
        }

        // Flash control is added if device supports it.
        if(characteristics.get(SCameraCharacteristics.FLASH_INFO_AVAILABLE))
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("Flash mode",
                            SCaptureRequest.FLASH_MODE,
                            Arrays.asList("Off", "Single", "Torch"),
                            Arrays.asList(SCaptureRequest.FLASH_MODE_OFF,
                                    SCaptureRequest.FLASH_MODE_SINGLE,
                                    SCaptureRequest.FLASH_MODE_TORCH),
                            Arrays.asList(SCaptureRequest.FLASH_MODE_OFF,
                                    SCaptureRequest.FLASH_MODE_SINGLE,
                                    SCaptureRequest.FLASH_MODE_TORCH)),
                    layoutParams);
        else
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("Flash mode"), layoutParams);

        // AWB mode is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("AWB mode",
                        SCaptureRequest.CONTROL_AWB_MODE,
                        Arrays.asList("Auto", "Incandescent", "Fluorescent", "Warm fluorescent", "Daylight", "Cloudy daylight", "Twilight", "Shade"),
                        Arrays.asList(SCaptureRequest.CONTROL_AWB_MODE_AUTO,
                                SCaptureRequest.CONTROL_AWB_MODE_INCANDESCENT,
                                SCaptureRequest.CONTROL_AWB_MODE_FLUORESCENT,
                                SCaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT,
                                SCaptureRequest.CONTROL_AWB_MODE_DAYLIGHT,
                                SCaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT,
                                SCaptureRequest.CONTROL_AWB_MODE_TWILIGHT,
                                SCaptureRequest.CONTROL_AWB_MODE_SHADE),
                        asList(characteristics.get(SCameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES))),
                layoutParams);

        // AWB lock is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("AWB Lock",
                        SCaptureRequest.CONTROL_AWB_LOCK,
                        Arrays.asList("Unlock", "Lock"),
                        Arrays.asList(false, true),
                        Arrays.asList(false, true)),
                layoutParams);

        // Effect mode is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("Effect mode",
                        SCaptureRequest.CONTROL_EFFECT_MODE,
                        Arrays.asList("Off", "Mono", "Negative", "Solarize", "Sepia", "Posterize", "Whiteboard", "Blackboard", "Aqua"),
                        Arrays.asList(SCaptureRequest.CONTROL_EFFECT_MODE_OFF,
                                SCaptureRequest.CONTROL_EFFECT_MODE_MONO,
                                SCaptureRequest.CONTROL_EFFECT_MODE_NEGATIVE,
                                SCaptureRequest.CONTROL_EFFECT_MODE_SOLARIZE,
                                SCaptureRequest.CONTROL_EFFECT_MODE_SEPIA,
                                SCaptureRequest.CONTROL_EFFECT_MODE_POSTERIZE,
                                SCaptureRequest.CONTROL_EFFECT_MODE_WHITEBOARD,
                                SCaptureRequest.CONTROL_EFFECT_MODE_BLACKBOARD,
                                SCaptureRequest.CONTROL_EFFECT_MODE_AQUA),
                        asList(characteristics.get(SCameraCharacteristics.CONTROL_AVAILABLE_EFFECTS))),
                layoutParams);

        // jpeg quality is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("Jpeg Quality",
                        SCaptureRequest.JPEG_QUALITY,
                        new Range<Byte>((byte)1, (byte)100)),
                layoutParams);

        // jpeg thumbnail is available for all device
        {
            List<Size> thumbnailSizeList = Arrays.asList(characteristics.get(SCameraCharacteristics.JPEG_AVAILABLE_THUMBNAIL_SIZES));
            List<String> thumbnailSizeStringList = new ArrayList<String>();

            for(Size size : thumbnailSizeList) thumbnailSizeStringList.add(size.toString());

            ((LinearLayout) dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("Thumbnail size",
                            SCaptureRequest.JPEG_THUMBNAIL_SIZE,
                            thumbnailSizeStringList,
                            thumbnailSizeList,
                            thumbnailSizeList),
                    layoutParams);
        }

        // jpeg thumbnail quality is available for all device
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("Thumbnail Quality",
                        SCaptureRequest.JPEG_THUMBNAIL_QUALITY,
                        new Range<Byte>((byte)1, (byte)100)),
                layoutParams);

        // max zoom is available for all device
        if(characteristics.get(SCameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM) > 1) {
            List<String> zoomRatioStringList = new ArrayList<String>();
            List<Rect> zoomRatioValueList = new ArrayList<Rect>();
            Rect sensorSize = characteristics.get(SCameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);

            // Make crop rect starting from 1.0x to max zoom ratio by 0.5 step.
            for(float ratio = 1.0f; ratio <= characteristics.get(SCameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM); ratio+=0.5f) {
                zoomRatioStringList.add("" + ratio + "x");
                int zoomWidth = (int)Math.floor(sensorSize.width() / ratio);
                int zoomHeight = (int)Math.floor(sensorSize.height() / ratio);
                zoomRatioValueList.add(new Rect( (sensorSize.width() - zoomWidth) / 2, (sensorSize.height() - zoomHeight) / 2, (sensorSize.width() + zoomWidth) / 2, (sensorSize.height() + zoomHeight) / 2));
            }
            mDefaultZoom = zoomRatioValueList.get(0);

            ((LinearLayout) dialogView.findViewById(R.id.settinglist)).addView(
                    addSettingItem("Zoom ratio",
                            SCaptureRequest.SCALER_CROP_REGION,
                            zoomRatioStringList,
                            zoomRatioValueList,
                            zoomRatioValueList),
                    layoutParams);
        } else {
            ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("Zoom ratio"), layoutParams);
        }

        // tone map control is available if device reports POST_PROCESSING capability. double check from available key list.
        if(asList(characteristics.get(SCameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)).contains(SCameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING) &&
                characteristics.getKeys().contains(SCameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES)) {

                ((LinearLayout) dialogView.findViewById(R.id.settinglist)).addView(
                        addSettingItem("Tone map mode",
                                SCaptureRequest.TONEMAP_MODE,
                                Arrays.asList("Contrast curve", "Fast", "High Quality"),
                                Arrays.asList(SCaptureRequest.TONEMAP_MODE_CONTRAST_CURVE,
                                        SCaptureRequest.TONEMAP_MODE_FAST,
                                        SCaptureRequest.TONEMAP_MODE_HIGH_QUALITY),
                                asList(characteristics.get(SCameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES))),
                        layoutParams);

                List<TonemapCurve> toneMapCurveList = new ArrayList<TonemapCurve>();
                List<String> toneMapCurveStringList = new ArrayList<String>();

                // Linear
                toneMapCurveList.add(new TonemapCurve(new float[]{0f, 0f, 1f, 1f}, new float[]{0f, 0f, 1f, 1f}, new float[]{0f, 0f, 1f, 1f}));
                toneMapCurveStringList.add("Linear");

                // Invert
                toneMapCurveList.add(new TonemapCurve(new float[]{0f, 1f, 1f, 0f}, new float[]{0f, 1f, 1f, 0f}, new float[]{0f, 1f, 1f, 0f}));
                toneMapCurveStringList.add("Invert");

                // sRGB, max curve points must be at least 64. 16 points is good to go. (SCameraCharacteristics.TONEMAP_MAX_CURVE_POINTS)
                toneMapCurveList.add(new TonemapCurve(
                        new float[]{0.0000f, 0.0000f, 0.0667f, 0.2864f, 0.1333f, 0.4007f, 0.2000f, 0.4845f, 0.2667f, 0.5532f, 0.3333f, 0.6125f, 0.4000f, 0.6652f, 0.4667f, 0.7130f, 0.5333f, 0.7569f, 0.6000f, 0.7977f, 0.6667f, 0.8360f, 0.7333f, 0.8721f, 0.8000f, 0.9063f, 0.8667f, 0.9389f, 0.9333f, 0.9701f, 1.0000f, 1.0000f},
                        new float[]{0.0000f, 0.0000f, 0.0667f, 0.2864f, 0.1333f, 0.4007f, 0.2000f, 0.4845f, 0.2667f, 0.5532f, 0.3333f, 0.6125f, 0.4000f, 0.6652f, 0.4667f, 0.7130f, 0.5333f, 0.7569f, 0.6000f, 0.7977f, 0.6667f, 0.8360f, 0.7333f, 0.8721f, 0.8000f, 0.9063f, 0.8667f, 0.9389f, 0.9333f, 0.9701f, 1.0000f, 1.0000f},
                        new float[]{0.0000f, 0.0000f, 0.0667f, 0.2864f, 0.1333f, 0.4007f, 0.2000f, 0.4845f, 0.2667f, 0.5532f, 0.3333f, 0.6125f, 0.4000f, 0.6652f, 0.4667f, 0.7130f, 0.5333f, 0.7569f, 0.6000f, 0.7977f, 0.6667f, 0.8360f, 0.7333f, 0.8721f, 0.8000f, 0.9063f, 0.8667f, 0.9389f, 0.9333f, 0.9701f, 1.0000f, 1.0000f}));
                toneMapCurveStringList.add("sRGB");

                // Calculate gamma curve 1.6 ~ 2.2
                for (float gamma = 1.6f; gamma < 2.3f; gamma += 0.2f) {
                    // max curve points must be at least 64. 16 points is good to go.
                    float[] curve = new float[32];

                    for (int i = 0; i < curve.length/2; i++) {
                        curve[2 * i] = (float) i / (curve.length / 2  - 1);
                        curve[2 * i + 1] = (float) Math.pow((float) i / (curve.length / 2  - 1), 1f / gamma);
                    }
                    toneMapCurveList.add(new TonemapCurve(curve, curve, curve));
                    toneMapCurveStringList.add(String.format("1/%.1f", gamma));
                }

                ((LinearLayout) dialogView.findViewById(R.id.settinglist)).addView(
                        addSettingItem("Tone map curve",
                                SCaptureRequest.TONEMAP_CURVE,
                                toneMapCurveStringList,
                                toneMapCurveList,
                                toneMapCurveList),
                        layoutParams);
            } else {
                ((LinearLayout) dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("Tone map mode"), layoutParams);
                ((LinearLayout) dialogView.findViewById(R.id.settinglist)).addView(addSettingItem("Tone map curve"), layoutParams);
            }
        //}

        // Add Image saving format option
        ((LinearLayout)dialogView.findViewById(R.id.settinglist)).addView(
                addSettingItem("Save as",
                        Integer.valueOf(SettingItem.SETTING_TYPE_IMAGE_FORMAT),
                        Arrays.asList("JPEG", "DNG"),
                        Arrays.asList(ImageFormat.JPEG,
                                ImageFormat.RAW_SENSOR),
                        availableImageFormat),
                layoutParams);

        mDialog = new AlertDialog.Builder(mActivity)
            .setView(dialogView)
            .setTitle("Setting")
            .create();
    }

    /**
     * Convert int array to Integer list.
     */
    private List<Integer> asList(int[] array) {
        List<Integer> list = new ArrayList<Integer>();

        for (int value : array)  list.add(value);
        return list;
    }

    /**
     * Add disabled(not supported) SettingItem UI to view.
     * Those setting item only display grayed out text label.
     */
    private View addSettingItem(String name) {
        DisabledSettingItem settingItem = new DisabledSettingItem(mActivity, name);
        return settingItem.getView();
    }

    /**
     * Add Integer list type SettingItem UI to view.
     */
    private View addSettingItem(String name, Integer key, List<String> valueNameList, List<Integer> valueList, List<Integer> availableValueList) {
        ListSettingItem<Integer, Integer> settingItem = new ListSettingItem<Integer, Integer>(mActivity, name, key, valueNameList, valueList, availableValueList);
        settingItem.setOnSettingItemValueChangedListener(this);
        settingItem.setEnabled(true);

        mSettingItemMap.put(key, settingItem);

        return settingItem.getView();
    }

    /**
     * Add SCaptureRequest.Key/Value list type SettingItem UI to view.
     */
    private <V> View addSettingItem(String name, SCaptureRequest.Key<V> key, List<String> valueNameList, List<V> valueList, List<V> availableValueList) {
        ListSettingItem<SCaptureRequest.Key<V>, V> settingItem = new ListSettingItem<SCaptureRequest.Key<V>, V>(mActivity, name, key, valueNameList, valueList, availableValueList);
        settingItem.setOnSettingItemValueChangedListener(this);
        settingItem.setEnabled(true);

        mSettingItemMap.put(key, settingItem);

        return settingItem.getView();
    }

    /**
     * Add SCaptureRequest.Key/Range type SettingItem UI to view.
     */
    private <V extends Number & java.lang.Comparable<? super V>> View addSettingItem(String name, SCaptureRequest.Key<V> key, Range<V> range) {
        RangeSettingItem<SCaptureRequest.Key<V>, V> settingItem = new RangeSettingItem<SCaptureRequest.Key<V>, V>(mActivity, name, key, range);
        settingItem.setOnSettingItemValueChangedListener(this);
        settingItem.setEnabled(true);

        mSettingItemMap.put(key, settingItem);

        return settingItem.getView();
    }

    /**
     * Setting UI has been changed.
     * @param key Key for Setting UI
     * @param value Changed value.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <K, V> void onSettingItemValueChanged(K key, V value) {

        // Control dependency between Keys.
        // If one SettingItem A overrides other SettingItem B, B will be dimmed/disabled by following routine.
        if (key == SCaptureRequest.CONTROL_AF_MODE) {
            switch ((Integer) value) {
                // If AF mode is not 'off', focus distance control/phase af will be disabled.
                case SCaptureRequest.CONTROL_AF_MODE_OFF:
                    setDim(false, SCaptureRequest.LENS_FOCUS_DISTANCE, null);
                    setDim(true, SCaptureRequest.PHASE_AF_MODE, SCaptureRequest.PHASE_AF_MODE_OFF);
                    break;
                default:
                    setDim(true, SCaptureRequest.LENS_FOCUS_DISTANCE, null);
                    setDim(false, SCaptureRequest.PHASE_AF_MODE, null);
                    break;
            }
        } else if (key == SCaptureRequest.PHASE_AF_MODE) {

        } else if (key == SCaptureRequest.LENS_FOCUS_DISTANCE) {

        } else if (key == SCaptureRequest.CONTROL_SCENE_MODE) {
            switch ((Integer) value) {
                // Scene mode other than face priority will ignore/override AF/AW/AWB mode value.
                case SCaptureRequest.CONTROL_SCENE_MODE_DISABLED:
                    setDim(false, SCaptureRequest.CONTROL_AF_MODE, null);
                    setDim(false, SCaptureRequest.CONTROL_AE_MODE, null);
                    setDim(false, SCaptureRequest.CONTROL_AWB_MODE, null);
                    break;
                case SCaptureRequest.CONTROL_SCENE_MODE_FACE_PRIORITY:
                    // AE_MODE off will not work under FACE_PRIORITY scene mode in Nexus 6.
                    if(!Build.MODEL.equals("Nexus 6")) setDim(false, SCaptureRequest.CONTROL_AE_MODE, null);
                    else setDim(true, SCaptureRequest.CONTROL_AE_MODE, SCaptureRequest.CONTROL_AE_MODE_ON);
                    setDim(false, SCaptureRequest.CONTROL_AF_MODE, null);
                    setDim(false, SCaptureRequest.CONTROL_AWB_MODE, null);
                    break;
                default:
                    setDim(true, SCaptureRequest.CONTROL_AF_MODE, SCaptureRequest.CONTROL_AF_MODE_AUTO);
                    setDim(true, SCaptureRequest.CONTROL_AE_MODE, SCaptureRequest.CONTROL_AE_MODE_ON);
                    setDim(true, SCaptureRequest.CONTROL_AWB_MODE, SCaptureRequest.CONTROL_AWB_MODE_AUTO);
                    break;
            }
        } else if (key == SCaptureRequest.STATISTICS_FACE_DETECT_MODE) {


        } else if (key == SCaptureRequest.CONTROL_AE_MODE) {
            switch ((Integer) value) {
                // If AF mode if 'off', AE lock, AE compensation, metering mode, AE anti-baning mode, Target FPS range will be ignored.
                // But you have a control over ISO, shutter speed, flash mode
                case SCaptureRequest.CONTROL_AE_MODE_OFF:
                    setDim(true, SCaptureRequest.CONTROL_AE_LOCK, false);
                    setDim(true, SCaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, 0);
                    setDim(true, SCaptureRequest.METERING_MODE, null);
                    setDim(true, SCaptureRequest.CONTROL_AE_ANTIBANDING_MODE, SCaptureRequest.CONTROL_AE_ANTIBANDING_MODE_OFF);
                    setDim(true, SCaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, null);

                    setDim(false, SCaptureRequest.SENSOR_SENSITIVITY, null);
                    setDim(false, SCaptureRequest.SENSOR_EXPOSURE_TIME, null);
                    setDim(false, SCaptureRequest.FLASH_MODE, null);
                    break;
                case SCaptureRequest.CONTROL_AE_MODE_ON:
                case SCaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH:
                case SCaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE:
                case SCaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH:

                    // Flash mode is overridden base on AE mode, aside from AE_MODE_OFF/AE_MODE_ON
                    switch((Integer) value) {
                        case SCaptureRequest.CONTROL_AE_MODE_ON:
                            setDim(false, SCaptureRequest.FLASH_MODE, null);
                            break;
                        case SCaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH:
                        case SCaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE:
                        case SCaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH:
                            // FLASH_MODE will be ignored under these ae mode. Just for UI.
                            setDim(true, SCaptureRequest.FLASH_MODE, SCaptureRequest.FLASH_MODE_SINGLE);
                            break;
                    }

                    setDim(false, SCaptureRequest.CONTROL_AE_LOCK, null);
                    setDim(false, SCaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, null);
                    setDim(false, SCaptureRequest.METERING_MODE, null);
                    setDim(false, SCaptureRequest.CONTROL_AE_ANTIBANDING_MODE, null);
                    setDim(false, SCaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, null);

                    // Aside from AE_MODE_OFF, other mode will disable ISO, shutter speed.
                    setDim(true, SCaptureRequest.SENSOR_SENSITIVITY, null);
                    setDim(true, SCaptureRequest.SENSOR_EXPOSURE_TIME, null);
                    break;
            }
        } else if(key == SCaptureRequest.CONTROL_AE_ANTIBANDING_MODE) {

        } else if (key == SCaptureRequest.CONTROL_AE_LOCK) {

        } else if (key == SCaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION) {

        } else if (key == SCaptureRequest.SENSOR_SENSITIVITY) {

        } else if (key == SCaptureRequest.SENSOR_EXPOSURE_TIME) {

        } else if (key == SCaptureRequest.METERING_MODE) {

        } else if (key == SCaptureRequest.LENS_OPTICAL_STABILIZATION_MODE) {
            // If OIS is turned off, OIS operation mode has no meaning.
            switch ((Integer) value) {
                case SCaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_OFF:
                    setDim(true, SCaptureRequest.LENS_OPTICAL_STABILIZATION_OPERATION_MODE, null);
                    break;
                case SCaptureRequest.LENS_OPTICAL_STABILIZATION_MODE_ON:
                    setDim(false, SCaptureRequest.LENS_OPTICAL_STABILIZATION_OPERATION_MODE, null);
                    break;
            }
        } else if (key == SCaptureRequest.LENS_OPTICAL_STABILIZATION_OPERATION_MODE) {

        } else if (key == SCaptureRequest.FLASH_MODE) {

        } else if (key == SCaptureRequest.CONTROL_AWB_MODE) {
            // If AWB is turned off, AWB lock control has no meaning.
            switch ((Integer) value) {
                case SCaptureRequest.CONTROL_AWB_MODE_AUTO:
                    setDim(false, SCaptureRequest.CONTROL_AWB_LOCK, null);
                    break;
                default:
                    setDim(true, SCaptureRequest.CONTROL_AWB_LOCK, false);
            }
        } else if (key == SCaptureRequest.CONTROL_AWB_LOCK) {

        } else if (key == SCaptureRequest.CONTROL_EFFECT_MODE) {

        } else if (key == SCaptureRequest.JPEG_QUALITY) {

        } else if (key == SCaptureRequest.JPEG_THUMBNAIL_SIZE) {

        } else if (key == SCaptureRequest.JPEG_THUMBNAIL_QUALITY) {

        } else if (key == SCaptureRequest.SCALER_CROP_REGION) {

        } else if (key == SCaptureRequest.TONEMAP_MODE) {
            switch ((Integer)value) {
                case SCaptureRequest.TONEMAP_MODE_CONTRAST_CURVE:
                    setDim(false, SCaptureRequest.TONEMAP_CURVE, null);
                    break;
                default:
                    setDim(true, SCaptureRequest.TONEMAP_CURVE, null);
                    break;
            }
        } else if (key == SCaptureRequest.TONEMAP_CURVE) {

        } else if (key instanceof Integer && (Integer)key == SettingItem.SETTING_TYPE_IMAGE_FORMAT) {
            switch ((Integer)value) {
                case ImageFormat.JPEG:
                    setDim(false, SCaptureRequest.SCALER_CROP_REGION, null);
                    break;
                case ImageFormat.RAW_SENSOR:
                    // crop region is applied after the RAW to other color space (e.g. YUV) conversion.
                    setDim(true, SCaptureRequest.SCALER_CROP_REGION, mDefaultZoom);
                    break;
            }
        } else if (key instanceof Integer && (Integer)key == SettingItem.SETTING_TYPE_CAMERA_FACING) {

        }

        // Configure the SCaptureRequest.Builder based on changed setting value
        if(mBuilders != null) {
            for (SCaptureRequest.Builder builder : mBuilders) {
                if (key instanceof SCaptureRequest.Key) {
                    if (key == SCaptureRequest.FLASH_MODE &&
                            (Integer) value == SCaptureRequest.FLASH_MODE_SINGLE &&
                            builder.get(SCaptureRequest.CONTROL_CAPTURE_INTENT) == SCaptureRequest.CONTROL_CAPTURE_INTENT_PREVIEW) {
                        builder.set((SCaptureRequest.Key<V>) key, (V) Integer.valueOf(SCaptureRequest.FLASH_MODE_OFF));
                    } else if (key == SCaptureRequest.CONTROL_SCENE_MODE) {
                        if ((Integer) value != SCaptureRequest.CONTROL_SCENE_MODE_DISABLED) {
                            // to use scene mode, we need to change CONTROL_MODE to CONTROL_MODE_USE_SCENE_MODE.
                            builder.set(SCaptureRequest.CONTROL_MODE, SCaptureRequest.CONTROL_MODE_USE_SCENE_MODE);
                        } else {
                            // if scene mode is turned off, revert it to original value which is AUTO.
                            builder.set(SCaptureRequest.CONTROL_MODE, SCaptureRequest.CONTROL_MODE_AUTO);
                        }
                        builder.set((SCaptureRequest.Key<V>) key, value);
                    } else {
                        builder.set((SCaptureRequest.Key<V>) key, value);
                    }
                } else if(key instanceof Integer) {
                    if ((Integer)key == SettingItem.SETTING_TYPE_CAMERA_FACING) {

                    } else if((Integer)key == SettingItem.SETTING_TYPE_IMAGE_FORMAT) {

                    }
                }
            }
        }

        // Notify to listener that we have changed setting value.
        if (mListener != null && mConfigured) {
            if(key instanceof SCaptureRequest.Key) mListener.onCameraSettingUpdated(SettingItem.SETTING_TYPE_REQUEST_KEY, 0);
            else mListener.onCameraSettingUpdated((Integer)key, (Integer)value);
        }
    }

    /**
     * Dim the setting UI. If one setting item (A) is changed which overrides other setting item (B), then B will be dimmed/disabled via this function.
     * No circular or conflicting hierarchy in 'override' is considered.
     */
    @SuppressWarnings("unchecked")
    private <T> void setDim(boolean isDim, SCaptureRequest.Key<T> key, T value) {
        if(!mSettingItemMap.containsKey(key)) return;

        if(isDim) {
            SettingItem<SCaptureRequest.Key<T>, T> item = (SettingItem<SCaptureRequest.Key<T>, T>) mSettingItemMap.get(key);
            if(value != null) {
                // do not override saved original value.
                if(!mDimMap.containsKey(key)) mDimMap.put(key, item.getValue());
                item.setValue(value);
            } // null value means just UI dimming. no setting change.
        } else {
            // if value is overridden during UI dimming, revert to it original value.
            if(mDimMap.containsKey(key)) {
                ((SettingItem<SCaptureRequest.Key<T>, T>) mSettingItemMap.get(key)).setValue( (T)mDimMap.get(key) );
            }
            mDimMap.remove(key);
        }

        // dimming UI
        mSettingItemMap.get(key).setEnabled(!isDim);
    }

    /**
     * Configure this SettingDialog with given camera characteristic and request builder.
     * Every time setting value is changed, Those builders will be configured based on the setting values.
     */
    @SuppressWarnings("unchecked")
    public void configure(SCameraCharacteristics characteristics, int lensFacing, List<Integer> availableLensFacing,
                          int imageFormat, List<Integer> availableImageFormat, SCaptureRequest.Builder... builders) {
        mConfigured = false;
        mDimMap.clear();
        mSettingItemMap.clear();
        
        initDialog(characteristics, availableLensFacing, availableImageFormat);

        mBuilders = builders;

        for(SCaptureRequest.Builder builder : builders) {
            // Synchronize UI and SCaptureRequest.Builder
            if(builder.get(SCaptureRequest.CONTROL_CAPTURE_INTENT) == SCaptureRequest.CONTROL_CAPTURE_INTENT_PREVIEW) {
                for(SettingItem<?, ?> item : mSettingItemMap.values()) {
                    if(item.getKey() instanceof SCaptureRequest.Key) {
                        item.setInitialValue(builder.get( (SCaptureRequest.Key)item.getKey() ));
                    } else if(item.getKey() instanceof Integer && (Integer)item.getKey() == SettingItem.SETTING_TYPE_CAMERA_FACING) {
                        item.setInitialValue(lensFacing);
                    } else if(item.getKey() instanceof Integer && (Integer)item.getKey() == SettingItem.SETTING_TYPE_IMAGE_FORMAT) {
                        item.setInitialValue(imageFormat);
                    }
                }
            }
        }

        mConfigured = true;
        if(mListener != null) mListener.onCameraSettingUpdated(SettingItem.SETTING_TYPE_REQUEST_KEY, 0);
    }
}
