package fsexample.fsexample.common;

import java.util.ArrayList;

/**
 * @author Syntarou YOSHIDA
 */
public class MXMidiStatic {
    public static final String PREFIX_CH = " '";

    public static final int COMMAND_CH_NOTEOFF = 0x80;
    public static final int COMMAND_CH_NOTEON = 0x90;
    public static final int COMMAND_CH_POLYPRESSURE = 0xa0;
    public static final int COMMAND_CH_CONTROLCHANGE = 0xb0;
    public static final int COMMAND_CH_PROGRAMCHANGE = 0xc0;
    public static final int COMMAND_CH_CHANNELPRESSURE = 0xd0;
    public static final int COMMAND_CH_PITCHWHEEL = 0xe0;

    public static final int COMMAND_SYSEX = 0xf0;
    public static final int COMMAND_MIDITIMECODE = 0xf1;
    public static final int COMMAND_SONGPOSITION = 0xf2;
    public static final int COMMAND_SONGSELECT = 0xf3;
    public static final int COMMAND_F4 = 0xf4;
    public static final int COMMAND_F5 = 0xf5;
    public static final int COMMAND_TUNEREQUEST = 0xf6;
    public static final int COMMAND_SYSEX_END = 0xf7;
    public static final int COMMAND_TRANSPORT_MIDICLOCK = 0xf8;
    public static final int COMMAND_F9 = 0xf9;
    public static final int COMMAND_TRANSPORT_START = 0xfa;
    public static final int COMMAND_TRANSPORT_CONTINUE = 0xfb;
    public static final int COMMAND_TRANSPORT_STOP = 0xfc;
    public static final int COMMAND_FD = 0xfd;
    public static final int COMMAND_ACTIVESENSING = 0xfe;
    public static final int COMMAND_META_OR_RESET = 0xff;

    public static final int COMMAND2_NONE = 0x0000; /* no param */
    public static final int COMMAND2_CH_RPN = 0x5500; /* msb lsb datamsb datalsb */
    public static final int COMMAND2_CH_NRPN = 0x5600; /* msb lsb datamsb datalsb */
    public static final int COMMAND2_CH_PROGRAM_INC = 0x6000; /* no param */
    public static final int COMMAND2_CH_PROGRAM_DEC = 0x6100; /* no param */
    public static final int COMMAND2_CH_PITCH_MSBLSB = 0x6200; /* @PB MSB LSB -> translate E0 LSB MSB */
    public static final int COMMAND2_CH_PROGRAMCHANGE_BANK = 0x6300; /* @PC_BANK pc, msb, lsb */
    public static final int COMMAND2_SYSEX = 0x7000;
    public static final int COMMAND2_SUB_VOLUME = 0x7100;
    public static final int COMMAND2_SUB_PAN = 0x7200;

    public static final int CCXML_NONE = 0x100;
    public static final int CCXML_VL = 0x200;
    public static final int CCXML_VH = 0x300;
    public static final int CCXML_GL = 0x400;
    public static final int CCXML_GH = 0x500;
    public static final int CCXML_CH = 0x600;
    public static final int CCXML_1CH = 0x700;
    public static final int CCXML_2CH = 0x800;
    public static final int CCXML_3CH = 0x900;
    public static final int CCXML_PCH = 0xA00;
    public static final int CCXML_1RCH = 0xB00;
    public static final int CCXML_2RCH = 0xC00;
    public static final int CCXML_3RCH = 0xD00;
    public static final int CCXML_4RCH = 0xE00;
    public static final int CCXML_VF1 = 0xF00;
    public static final int CCXML_VF2 = 0x1000;
    public static final int CCXML_VF3 = 0x1100;
    public static final int CCXML_VF4 = 0x1200;
    public static final int CCXML_VPGL = 0x1300;
    public static final int CCXML_VPGH = 0x1400;
    public static final int CCXML_CCNUM = 0x1500;
    public static final int CCXML_RSCTRT1 = 0x1A00;
    public static final int CCXML_RSCTRT2 = 0x1B00;
    public static final int CCXML_RSCTRT3 = 0x1C00;
    public static final int CCXML_RSCTRT1P = 0x1D00;
    public static final int CCXML_RSCTRT2P = 0x1E00;
    public static final int CCXML_RSCTRT3P = 0x1F00;
    public static final int CCXML_RSCTPT1 = 0x2000;
    public static final int CCXML_RSCTPT2 = 0x2100;
    public static final int CCXML_RSCTPT3 = 0x2200;
    public static final int CCXML_RSCTPT1P = 0x2300;
    public static final int CCXML_RSCTPT2P = 0x2400;
    public static final int CCXML_RSCTPT3P = 0x2500;
    public static final int CCXML_CHECKSUM_END = 0x2600;
    public static final int CCXML_CHECKSUM_START = 0x2700;

    public static final int DATA1_CC_BANKSELECT = 0;
    public static final int DATA1_CC_MODULATION = 1;
    public static final int DATA1_CC_BREATH = 2;
    public static final int DATA1_CC_3 = 3;
    public static final int DATA1_CC_FOOTCONTROL = 4;
    public static final int DATA1_CC_PORTAMENTTIME = 5;
    public static final int DATA1_CC_DATAENTRY = 6;
    public static final int DATA1_CC_DATAENTRY2 = 6 + 32;
    public static final int DATA1_CC_CHANNEL_VOLUME = 7;
    public static final int DATA1_CC_BALANCE = 8;
    public static final int DATA1_CC_9 = 9;
    public static final int DATA1_CC_PANPOT = 10;
    public static final int DATA1_CC_EXPRESSION = 11;
    public static final int DATA1_CC_EFFECTCONTROL1 = 12;
    public static final int DATA1_CC_EFFECTCONTROL2 = 13;
    public static final int DATA1_CC_14 = 14;
    public static final int DATA1_CC_15 = 15;
    public static final int DATA1_CC_COMMON1 = 16;
    public static final int DATA1_CC_COMMON2 = 17;
    public static final int DATA1_CC_COMMON3 = 18;
    public static final int DATA1_CC_COMMON4 = 19;
    public static final int DATA1_CC_DAMPERPEDAL = 64;
    public static final int DATA1_CC_PORTAMENT = 65;
    public static final int DATA1_CC_FOOT_SOFTENUT = 66;
    public static final int DATA1_CC_FOOT_SOFT = 67;
    public static final int DATA1_CC_FOOT_LEGATO = 68;
    public static final int DATA1_CC_HOLD2_FREEZE = 69;
    public static final int DATA1_CC_SOUND_VALIATION = 70;
    public static final int DATA1_CC_SOUND_RESONANCE = 71;
    public static final int DATA1_CC_SOUND_RELEASETIME = 72;
    public static final int DATA1_CC_SOUND_ATTACKTIME = 73;
    public static final int DATA1_CC_SOUND_BLIGHTNESS = 74;
    public static final int DATA1_CC_SOUND_DECAYTIME = 75;
    public static final int DATA1_CC_SOUND_VIBRATE_RATE = 76;
    public static final int DATA1_CC_SOUND_VIBRATE_DEPTH = 77;
    public static final int DATA1_CC_SOUND_VIBRATE_DELAY = 78;
    public static final int DATA1_CC_79 = 79;
    public static final int DATA1_CC_COMMON5 = 80;
    public static final int DATA1_CC_COMMON6 = 81;
    public static final int DATA1_CC_COMMON7 = 82;
    public static final int DATA1_CC_COMMON8 = 83;
    public static final int DATA1_CC_CONTROL_SOURCENOTE = 84;
    public static final int DATA1_CC_85 = 85;
    public static final int DATA1_CC_86 = 86;
    public static final int DATA1_CC_87 = 87;
    public static final int DATA1_CC_VELOCITYHQ = 88;
    public static final int DATA1_CC_89 = 89;
    public static final int DATA1_CC_90 = 90;
    public static final int DATA1_CC_EFFECT1_REVERVE = 91;
    public static final int DATA1_CC_EFFECT2_TREMOLO = 92;
    public static final int DATA1_CC_EFFECT3_CHORUS = 93;
    public static final int DATA1_CC_EFFECT4_DETUNE = 94;
    public static final int DATA1_CC_EFFECT5_PHASER = 95;
    public static final int DATA1_CC_DATAINC = 96;
    public static final int DATA1_CC_DATADEC = 97;
    public static final int DATA1_CC_NRPN_LSB = 98;
    public static final int DATA1_CC_NRPN_MSB = 99;
    public static final int DATA1_CC_RPN_LSB = 100;
    public static final int DATA1_CC_RPN_MSB = 101;
    public static final int DATA1_CC_ALLSOUNDOFF = 120;
    public static final int DATA1_CC_RESET_ALLCTRLS = 121;
    public static final int DATA1_CC_LOCALCTRL = 122;
    public static final int DATA1_CC_ALLNOTEOFF = 123;
    public static final int DATA1_CC_OMNI_OFF = 124;
    public static final int DATA1_CC_OMNI_ON = 125;
    public static final int DATA1_CC_MONOMODE = 126;
    public static final int DATA1_CC_POLYMODE = 127;

    public static final int MSB3D_AZIMUTH_ANGLE = 0;
    public static final int MSB3D_ELEVATION_ANGLE = 1;
    public static final int MSB3D_GAIN = 2;
    public static final int MSB3D_DISTANCE_RATIO = 3;
    public static final int MSB3D_MAXIMUM_DISTANCE = 4;
    public static final int MSB3_REFERENCE_DISTANCE_RATIO = 6;
    public static final int MSB3D_PAN_SPREAD_ANGLE = 7;
    public static final int MSB3D_ROLL_ANGLE = 8;

    public static final int MSB0_PITCHBEND_QUALITY = 0;
    public static final int MSB0_PITCHBEND_CHANNELFINETUNING = 1;
    public static final int MSB0_PITCHBEND_CHANNELCOURSETUNING = 2;
    public static final int MSB0_PITCHBEND_TUNINPROGRAMCHANGE = 3;
    public static final int MSB0_PITCHBEND_TUNINGBANKSELECT = 4;
    public static final int MSB0_PITCHBEND_MODULATIONDEPTHRANGE = 5;

    protected static final String[] noteSymbols = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static final int[] gmReset = {0xf0, 0x7e, 0x7f, 0x09, 0x01, 0xf7};
    public static final int[] gsReset = {0xF0, 0x41, -1, 0x42, 0x12, 0x40, 0x00, 0x7F, 0x00, 0x41, 0xF7};
    public static final int[] xgReset = {0xF0, 0x43, -1, 0x4C, 0x00, 0x00, 0x7E, 0x00, 0xF7};
    public static final int[] masterVolume = {0xF0, 0x7f, 0x7F, 0x04, 0x01, 0x11, MXMidiStatic.CCXML_VL, 0xF7};

    public static boolean isReset(byte[] data) {
        if (checkReset(gmReset, data)) {
            return true;
        }
        if (checkReset(gsReset, data)) {
            return true;
        }
        if (checkReset(xgReset, data)) {
            return true;
        }
        return false;
    }

    private static boolean checkReset(int[] compare, byte[] data) {
        if (data.length == compare.length) {
            for (int i = 0; i < data.length; ++i) {
                int x = compare[i];
                if (x < 0) {
                    continue;
                }
                if ((data[i] & 0xff) == x) {
                    continue;
                }
                return false;
            }
            return true;
        }
        return false;
    }

    public static final String nameOfChannelMessage(int command) {
        switch (command) {
            case MXMidiStatic.COMMAND_CH_NOTEON:
                return "ON   ";
            case MXMidiStatic.COMMAND_CH_NOTEOFF:
                return "OFF  ";
            case MXMidiStatic.COMMAND_CH_POLYPRESSURE:
                return "PolyP";
            case MXMidiStatic.COMMAND_CH_CONTROLCHANGE:
                return "CC   ";
            case MXMidiStatic.COMMAND_CH_PROGRAMCHANGE:
                return "PROG ";
            case MXMidiStatic.COMMAND_CH_CHANNELPRESSURE:
                return "Press";
            case MXMidiStatic.COMMAND_CH_PITCHWHEEL:
                return "PITCH";
        }
        return null;
    }

    public static String nameOfNote(int noteNo) {
        int oct = (noteNo / 12) - 1;
        noteNo = noteNo % 12;
        return "" + noteSymbols[noteNo] + Integer.toString(oct);
    }

    static int[] alphaToNote = {9, 11, 0, 2, 4, 5, 7};

    public static int noteOfName(String note) {
        try {
            char ch = note.charAt(0);
            int base = -1;
            int index = 0;
            if (ch >= 'a' && ch <= 'z') {
                ch += 'A' - 'a';
            }
            if (ch >= 'A' && ch <= 'G') {
                int key = alphaToNote[ch - 'A'];
                index++;
                if (note.charAt(index) == '#') {
                    key = key + 1;
                    index++;
                }
                int oct = Integer.parseInt(note.substring(index));
                key = key + (oct + 1) * 12;
                return key;
            }
            return -1;
        } catch (Throwable ex) {
            return -1;
        }
    }

    public static final String nameOfControlChange(int data1cc) {
        String namePrefix = "";
        switch (data1cc) {
            case MXMidiStatic.DATA1_CC_BANKSELECT:
                return namePrefix + "BANK";
            case MXMidiStatic.DATA1_CC_MODULATION:
                return namePrefix + "MODW";
            case MXMidiStatic.DATA1_CC_BREATH:
                return namePrefix + "BRTH";
            case MXMidiStatic.DATA1_CC_3:
                return namePrefix + "CC03";
            case MXMidiStatic.DATA1_CC_FOOTCONTROL:
                return namePrefix + "FOOT";
            case MXMidiStatic.DATA1_CC_PORTAMENTTIME:
                return namePrefix + "PRTA";
            case MXMidiStatic.DATA1_CC_DATAENTRY:
                return namePrefix + "DATA ";
            case MXMidiStatic.DATA1_CC_DATAENTRY2:
                return namePrefix + "DATA2 ";
            case MXMidiStatic.DATA1_CC_CHANNEL_VOLUME:
                return namePrefix + "VOL ";
            case MXMidiStatic.DATA1_CC_BALANCE:
                return namePrefix + "BAL ";
            case MXMidiStatic.DATA1_CC_9:
                return namePrefix + "CC09";
            case MXMidiStatic.DATA1_CC_PANPOT:
                return namePrefix + "PAN ";
            case MXMidiStatic.DATA1_CC_EXPRESSION:
                return namePrefix + "EXP ";
            case MXMidiStatic.DATA1_CC_EFFECTCONTROL1:
                return namePrefix + "EFC1";
            case MXMidiStatic.DATA1_CC_EFFECTCONTROL2:
                return namePrefix + "EFC2";
            case MXMidiStatic.DATA1_CC_14:
                return namePrefix + "CC14";
            case MXMidiStatic.DATA1_CC_15:
                return namePrefix + "CC15";
            case MXMidiStatic.DATA1_CC_COMMON1:
                return namePrefix + "CMN1";
            case MXMidiStatic.DATA1_CC_COMMON2:
                return namePrefix + "CMN2";
            case MXMidiStatic.DATA1_CC_COMMON3:
                return namePrefix + "CMN3";
            case MXMidiStatic.DATA1_CC_COMMON4:
                return namePrefix + "CMN4";
            case MXMidiStatic.DATA1_CC_DAMPERPEDAL:
                return namePrefix + "DUMP";
            case MXMidiStatic.DATA1_CC_PORTAMENT:
                return namePrefix + "PORT";
            case MXMidiStatic.DATA1_CC_FOOT_SOFTENUT:
                return namePrefix + "SFTE";
            case MXMidiStatic.DATA1_CC_FOOT_SOFT:
                return namePrefix + "SOFT";
            case MXMidiStatic.DATA1_CC_FOOT_LEGATO:
                return namePrefix + "REGD";
            case MXMidiStatic.DATA1_CC_HOLD2_FREEZE:
                return namePrefix + "FREZ";
            case MXMidiStatic.DATA1_CC_SOUND_VALIATION:
                return namePrefix + "VALI";
            case MXMidiStatic.DATA1_CC_SOUND_RESONANCE:
                return namePrefix + "TMBR";
            case MXMidiStatic.DATA1_CC_SOUND_RELEASETIME:
                return namePrefix + "RELS";
            case MXMidiStatic.DATA1_CC_SOUND_ATTACKTIME:
                return namePrefix + "ATCK";
            case MXMidiStatic.DATA1_CC_SOUND_BLIGHTNESS:
                return namePrefix + "BLIG";
            case MXMidiStatic.DATA1_CC_SOUND_DECAYTIME:
                return namePrefix + "DCAY";
            case MXMidiStatic.DATA1_CC_SOUND_VIBRATE_RATE:
                return namePrefix + "VRate";
            case MXMidiStatic.DATA1_CC_SOUND_VIBRATE_DEPTH:
                return namePrefix + "VDpth";
            case MXMidiStatic.DATA1_CC_SOUND_VIBRATE_DELAY:
                return namePrefix + "VDlay";
            case MXMidiStatic.DATA1_CC_79:
                return namePrefix + "CC79";
            case MXMidiStatic.DATA1_CC_COMMON5:
                return namePrefix + "CMN5";
            case MXMidiStatic.DATA1_CC_COMMON6:
                return namePrefix + "CMN6";
            case MXMidiStatic.DATA1_CC_COMMON7:
                return namePrefix + "CMN7";
            case MXMidiStatic.DATA1_CC_COMMON8:
                return namePrefix + "CMN8";
            case MXMidiStatic.DATA1_CC_CONTROL_SOURCENOTE:
                return namePrefix + "NOTE#";
            case MXMidiStatic.DATA1_CC_85:
                return namePrefix + "CC85";
            case MXMidiStatic.DATA1_CC_86:
                return namePrefix + "CC86";
            case MXMidiStatic.DATA1_CC_87:
                return namePrefix + "CC87";
            case MXMidiStatic.DATA1_CC_VELOCITYHQ:
                return namePrefix + "VEL2";
            case MXMidiStatic.DATA1_CC_89:
                return namePrefix + "CC89";
            case MXMidiStatic.DATA1_CC_90:
                return namePrefix + "CC90";
            case MXMidiStatic.DATA1_CC_EFFECT1_REVERVE:
                return namePrefix + "REVR";
            case MXMidiStatic.DATA1_CC_EFFECT2_TREMOLO:
                return namePrefix + "TRML";
            case MXMidiStatic.DATA1_CC_EFFECT3_CHORUS:
                return namePrefix + "CHOR";
            case MXMidiStatic.DATA1_CC_EFFECT4_DETUNE:
                return namePrefix + "DETU";
            case MXMidiStatic.DATA1_CC_EFFECT5_PHASER:
                return namePrefix + "PHAS";
            case MXMidiStatic.DATA1_CC_DATAINC:
                return namePrefix + "INC ";
            case MXMidiStatic.DATA1_CC_DATADEC:
                return namePrefix + "DEC ";
            case MXMidiStatic.DATA1_CC_NRPN_LSB:
                return namePrefix + "NRPN L";
            case MXMidiStatic.DATA1_CC_NRPN_MSB:
                return namePrefix + "NRPN M";
            case MXMidiStatic.DATA1_CC_RPN_LSB:
                return namePrefix + "RPN L";
            case MXMidiStatic.DATA1_CC_RPN_MSB:
                return namePrefix + "RPN M";
            case MXMidiStatic.DATA1_CC_ALLSOUNDOFF:
                return "AllOff";
            case MXMidiStatic.DATA1_CC_RESET_ALLCTRLS:
                return "ResetCC";
            case MXMidiStatic.DATA1_CC_LOCALCTRL:
                return "Local";
            case MXMidiStatic.DATA1_CC_ALLNOTEOFF:
                return "AllNoteOff";
            case MXMidiStatic.DATA1_CC_OMNI_OFF:
                return "OmniOff";
            case MXMidiStatic.DATA1_CC_OMNI_ON:
                return "OmniOn";
            case MXMidiStatic.DATA1_CC_MONOMODE:
                return "Mono";
            case MXMidiStatic.DATA1_CC_POLYMODE:
                return "Poly";
        }
        return "@CC#" + MXUtil.toHexString2(data1cc) + "h";
    }

    public static String nameOfSystemRealtimeMessage(int command) {
        switch (command) {
            case MXMidiStatic.COMMAND_TRANSPORT_MIDICLOCK:
                return "Clock";
            case MXMidiStatic.COMMAND_F9:
                return "#F9";
            case MXMidiStatic.COMMAND_TRANSPORT_START:
                return "Start";
            case MXMidiStatic.COMMAND_TRANSPORT_CONTINUE:
                return "Continue";
            case MXMidiStatic.COMMAND_TRANSPORT_STOP:
                return "Stop";
            case MXMidiStatic.COMMAND_FD:
                return "#FD";
            case MXMidiStatic.COMMAND_ACTIVESENSING:
                return "Active";
            case MXMidiStatic.COMMAND_META_OR_RESET:
                return "Meta/Reset";
        }
        return null;
    }

    public static String nameOfPortShort(int port) {
        if (port < 0) {
            return "-";
        }
        char portchar = (char) ('A' + port);
        return String.valueOf(portchar);
    }

    public static String nameOfPortInput(int port) {
        if (port < 0) {
            return "-";
        }
        char portchar = (char) ('A' + port);
        return String.valueOf(portchar);
    }

    public static String nameOfPortOutput(int port) {
        if (port < 0) {
            return "-";
        }
        char portchar = (char) ('A' + port);
        return String.valueOf(portchar);
    }

    public static String nameOfSystemCommonMessage(int status) {
        switch (status) {
            case MXMidiStatic.COMMAND_SYSEX:
                return "SysEx[";
            case MXMidiStatic.COMMAND_MIDITIMECODE:
                return "Time  ";
            case MXMidiStatic.COMMAND_SONGPOSITION:
                return "SngPos";
            case MXMidiStatic.COMMAND_SONGSELECT:
                return "SngNum";
            case MXMidiStatic.COMMAND_F4:
                return "Sys F4";
            case MXMidiStatic.COMMAND_F5:
                return "Sys F5";
            case MXMidiStatic.COMMAND_TUNEREQUEST:
                return "Tuner ";
            case MXMidiStatic.COMMAND_SYSEX_END:
                return "]EndEx";
        }
        return null;
    }

    public static String nameOfMessage(int status, int data1, int data2) {
        int command = status & 240;
        if (command == MXMidiStatic.COMMAND_CH_CONTROLCHANGE) {
            if (data1 == MXMidiStatic.DATA1_CC_DATAINC) {
                return "INC";
            }
            if (data1 == MXMidiStatic.DATA1_CC_DATADEC) {
                return "DEC";
            }
            if (data1 == MXMidiStatic.DATA1_CC_DATAENTRY) {
                return "DATA";
            }
            return nameOfControlChange(data1);
        }
        if (command >= 128 && command <= 224) {
            String name = nameOfChannelMessage(command);
            if (command == MXMidiStatic.COMMAND_CH_NOTEON || command == MXMidiStatic.COMMAND_CH_NOTEOFF || command == MXMidiStatic.COMMAND_CH_POLYPRESSURE) {
                return name;
            } else {
                return name;
            }
        }
        if (status >= 240 && status <= 247) {
            return nameOfSystemCommonMessage(status);
        }
        if (status >= 248 && status <= 255) {
            return nameOfSystemRealtimeMessage(status);
        }
        return "?";
    }

    public static int valueOfPortName(String capital) {
        char portchar = capital.charAt(0);
        if (portchar == '(') {
            return -1;
        }
        return (int) portchar - 'A';
    }


    public static int[] textToNoteList(String text) {
        ArrayList<String> list = new ArrayList<>();
        MXUtil.split(text, list, ' ');
        ArrayList<Integer> retList = new ArrayList<>();

        for (String noteText : list) {
            int note = noteOfName(noteText);
            if (note >= 0) {
                retList.add(note);
            }
        }

        int[] ret = new int[retList.size()];
        for (int x = 0; x < ret.length; ++x) {
            ret[x] = retList.get(x);
        }
        return ret;
    }

    public static String noteListToText(int[] note) {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < note.length; ++i) {
            str.append(MXMidiStatic.nameOfNote(note[i]));
            str.append(" ");
        }
        return str.toString();
    }


    static int[] MASTERVOLUME = new int[]{
            0xf0, 0x7F, 0x7F, 0x04, 0x01, 0x11, MXMidiStatic.CCXML_VL, 0xF7
    };
    static int[] PB_ = new int[]{
            MXMidiStatic.COMMAND2_CH_PITCH_MSBLSB, MXMidiStatic.CCXML_VH, MXMidiStatic.CCXML_VL
    };
    static int[] PITCH = new int[]{
            MXMidiStatic.COMMAND_CH_PITCHWHEEL, MXMidiStatic.CCXML_VL, MXMidiStatic.CCXML_VH
    };

}
