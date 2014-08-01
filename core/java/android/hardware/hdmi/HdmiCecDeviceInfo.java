/*
 * Copyright (C) 2014 The Android Open Source Project
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

package android.hardware.hdmi;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A class to encapsulate device information for HDMI-CEC. This container
 * include basic information such as logical address, physical address and
 * device type, and additional information like vendor id and osd name.
 * Also used to keep the information of non-CEC devices for which only
 * port ID, physical address are meaningful.
 *
 * @hide
 */
@SystemApi
public final class HdmiCecDeviceInfo implements Parcelable {

    /** TV device type. */
    public static final int DEVICE_TV = 0;

    /** Recording device type. */
    public static final int DEVICE_RECORDER = 1;

    /** Device type reserved for future usage. */
    public static final int DEVICE_RESERVED = 2;

    /** Tuner device type. */
    public static final int DEVICE_TUNER = 3;

    /** Playback device type. */
    public static final int DEVICE_PLAYBACK = 4;

    /** Audio system device type. */
    public static final int DEVICE_AUDIO_SYSTEM = 5;

    /** @hide Pure CEC switch device type. */
    public static final int DEVICE_PURE_CEC_SWITCH = 6;

    /** @hide Video processor device type. */
    public static final int DEVICE_VIDEO_PROCESSOR = 7;

    // Value indicating the device is not an active source.
    public static final int DEVICE_INACTIVE = -1;

    /**
     * Logical address used to indicate the source comes from internal device.
     * The logical address of TV(0) is used.
     */
    public static final int ADDR_INTERNAL = 0;

    /**
     * Physical address used to indicate the source comes from internal device.
     * The physical address of TV(0) is used.
     */
    public static final int PATH_INTERNAL = 0x0000;

    /** Invalid physical address (routing path) */
    public static final int PATH_INVALID = 0xFFFF;

    /** Invalid port ID */
    public static final int PORT_INVALID = -1;

    // Logical address, physical address, device type, vendor id and display name
    // are immutable value.
    private final int mLogicalAddress;
    private final int mPhysicalAddress;
    private final int mPortId;
    private final int mDeviceType;
    private final int mVendorId;
    private final String mDisplayName;
    private final boolean mIsCecDevice;

    /**
     * A helper class to deserialize {@link HdmiCecDeviceInfo} for a parcel.
     */
    public static final Parcelable.Creator<HdmiCecDeviceInfo> CREATOR =
            new Parcelable.Creator<HdmiCecDeviceInfo>() {
                @Override
                public HdmiCecDeviceInfo createFromParcel(Parcel source) {
                    int logicalAddress = source.readInt();
                    int physicalAddress = source.readInt();
                    int portId = source.readInt();
                    int deviceType = source.readInt();
                    int vendorId = source.readInt();
                    String displayName = source.readString();
                    return new HdmiCecDeviceInfo(logicalAddress, physicalAddress, portId,
                            deviceType, vendorId, displayName);
                }

                @Override
                public HdmiCecDeviceInfo[] newArray(int size) {
                    return new HdmiCecDeviceInfo[size];
                }
            };

    /**
     * Constructor. Used to initialize the instance for CEC device.
     *
     * @param logicalAddress logical address of HDMI-CEC device
     * @param physicalAddress physical address of HDMI-CEC device
     * @param portId HDMI port ID (1 for HDMI1)
     * @param deviceType type of device
     * @param vendorId vendor id of device. Used for vendor specific command.
     * @param displayName name of device
     * @hide
     */
    public HdmiCecDeviceInfo(int logicalAddress, int physicalAddress, int portId, int deviceType,
            int vendorId, String displayName) {
        mLogicalAddress = logicalAddress;
        mPhysicalAddress = physicalAddress;
        mPortId = portId;
        mDeviceType = deviceType;
        mDisplayName = displayName;
        mVendorId = vendorId;
        mIsCecDevice = true;
    }

    /**
     * Constructor. Used to initialize the instance for non-CEC device.
     *
     * @param physicalAddress physical address of HDMI device
     * @param portId HDMI port ID (1 for HDMI1)
     * @hide
     */
    public HdmiCecDeviceInfo(int physicalAddress, int portId) {
        mLogicalAddress = -1;
        mPhysicalAddress = physicalAddress;
        mPortId = portId;
        mDeviceType = DEVICE_RESERVED;
        mDisplayName = null;
        mVendorId = 0;
        mIsCecDevice = false;
    }

    /**
     * Return the logical address of the device.
     */
    public int getLogicalAddress() {
        return mLogicalAddress;
    }

    /**
     * Return the physical address of the device.
     */
    public int getPhysicalAddress() {
        return mPhysicalAddress;
    }

    /**
     * Return the port ID.
     */
    public int getPortId() {
        return mPortId;
    }

    /**
     * Return type of the device. For more details, refer constants between
     * {@link DEVICE_TV} and {@link DEVICE_INACTIVE}.
     */
    public int getDeviceType() {
        return mDeviceType;
    }

    /**
     * Return {@code true} if the device is of a type that can be an input source.
     */
    public boolean isSourceType() {
        return mDeviceType == DEVICE_PLAYBACK
                || mDeviceType == DEVICE_RECORDER
                || mDeviceType == DEVICE_TUNER;
    }

    /**
     * Return {@code true} if the device represents an HDMI-CEC device. {@code false}
     * if the device is either MHL or non-CEC device.
     */
    public boolean isCecDevice() {
        return mIsCecDevice;
    }

    /**
     * Return display (OSD) name of the device.
     */
    public String getDisplayName() {
        return mDisplayName;
    }

    /**
     * Return vendor id of the device. Vendor id is used to distinguish devices
     * built by other manufactures. This is required for vendor-specific command
     * on CEC standard.
     */
    public int getVendorId() {
        return mVendorId;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Serialize this object into a {@link Parcel}.
     *
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *        May be 0 or {@link Parcelable#PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mLogicalAddress);
        dest.writeInt(mPhysicalAddress);
        dest.writeInt(mPortId);
        dest.writeInt(mDeviceType);
        dest.writeInt(mVendorId);
        dest.writeString(mDisplayName);
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        if (isCecDevice()) {
            s.append("CEC: ");
            s.append("logical_address: ").append(mLogicalAddress).append(", ");
            s.append("physical_address: ").append(mPhysicalAddress).append(", ");
            s.append("port_id: ").append(mPortId).append(", ");
            s.append("device_type: ").append(mDeviceType).append(", ");
            s.append("vendor_id: ").append(mVendorId).append(", ");
            s.append("display_name: ").append(mDisplayName);
        } else {
            s.append("Non-CEC: ");
            s.append("physical_address: ").append(mPhysicalAddress).append(", ");
            s.append("port_id: ").append(mPortId).append(", ");
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof HdmiCecDeviceInfo)) {
            return false;
        }

        HdmiCecDeviceInfo other = (HdmiCecDeviceInfo) obj;
        return mLogicalAddress == other.mLogicalAddress
                && mPhysicalAddress == other.mPhysicalAddress
                && mPortId == other.mPortId
                && mDeviceType == other.mDeviceType
                && mVendorId == other.mVendorId
                && mDisplayName.equals(other.mDisplayName);
    }
}
