#include <jni.h>
#include <string>
#include <unistd.h>
#include <sstream>
#include "C:/github/fluidsynth-android24-noopenmp/include/fluidsynth.h"
#include <codecvt>
//#import <signal.h>
#include <android/log.h>
#include <thread>
#include <chrono>

#define FLUID_PEAK_ATTENUATION  960.0f
#define FLUID_CENTS_HZ_SIZE     1200
#define FLUID_VEL_CB_SIZE       128
#define FLUID_CB_AMP_SIZE       1441
#define FLUID_PAN_SIZE          1002di
JavaVM *_javaVM = nullptr;
jclass _javaClass = nullptr;
jmethodID _logging = nullptr;
jobject _javaObj = nullptr;
JNIEnv *_env = nullptr;

//#define USE_LOG 1
//#define HANDLE_FILE 1
//#define USE_LOG2 1

void log_java(JNIEnv* env, jobject obj, const char* message) {
#if 0
   __android_log_print(ANDROID_LOG_DEBUG, "Fluid", "%s", message);
    if (obj != nullptr && obj != _javaObj) {
        _javaObj = obj;
    }
    if (message != nullptr && env != nullptr && _logging != nullptr && _javaObj != nullptr) {
        const char *buf = message;
        jstring text = env->NewStringUTF(buf);
        env->CallVoidMethod(_javaObj, _logging, text);
    }
#endif
}

#if HANDLE_FILE

char* _freeBuf = nullptr;
char* _fileBuf = nullptr;
fpos_t _fileLength = 0;
fpos_t _cursor;

char* readFile(const char *filename) {
    if (_freeBuf != nullptr) {
        free(_freeBuf);
        _freeBuf = nullptr;
        _fileBuf = nullptr;
    }

    FILE *fp = fopen(filename, "rb");
    if (fseek(fp,0,SEEK_END) == 0) {
        fpos_t fsize;
        fsize = ftell(fp);
        if (fsize >= 1) {
            if (fseek(fp, 0, SEEK_SET) == 0) {
                char *buf = (char *) malloc(fsize + 100);
                if (buf == nullptr) {
                    fclose(fp);
                    return nullptr;
                }
                _freeBuf = buf;
                unsigned long ptr = ((unsigned  long)buf);
                while ((ptr % 16) != 0) {
                    ptr ++;
                }
                buf = (char *)ptr;
                if (fread(buf, 1, fsize, fp) == fsize) {
                    _fileBuf = buf;
                    _cursor = 0;
                    _fileLength = fsize;
                    fclose(fp);
                    return buf;
                }
                else {
                    free(_freeBuf);
                    _freeBuf = nullptr;
                }
            }
        }
    }
    _fileBuf = nullptr;
    _cursor = 0;
    _fileLength = 0;
    fclose(fp);
    return nullptr;
}

extern "C" {
    __cdecl void *my_open(const char *filename) {
        char *ptr = readFile(filename);
    #ifdef USE_LOG
        std::stringstream result;
        result << "my_open = " << _fileLength << "ptr = " << ((unsigned long)ptr);
        std::string str(result.str());
        log_java(_env, _javaObj, str.c_str());
    #endif
        return ptr;
    }

    __cdecl int my_read(void *buf, fluid_long_long_t count, void *handle) {
        if (handle != nullptr && handle == _fileBuf) {

            char *ptr = (char *) handle;
            if (ptr != _fileBuf) {
#ifdef USE_LOG2
                std::stringstream result;
                result << "read  broken";
                std::string str(result.str());
                log_java(_env, _javaObj, str.c_str());
#endif
                return -1;
            }
            if (_cursor > _fileLength) {
#ifdef USE_LOG
                std::stringstream result;
                result << "eof";
                std::string str(result.str());
                log_java(_env, _javaObj, str.c_str());
#endif
                return -1;
            }

            char *w = (char *) buf;
            long x = 0;
            for (int i = 0; (i < count) && (_cursor + x < _fileLength); ++i) {
                w[i] = ptr[_cursor + x];
                x ++;
            }
            _cursor += x;
#ifdef USE_LOG2
            std::stringstream result;
            long left = _fileLength - _cursor;
            result << "read  " << x << "then next is " << _cursor << "/" << _fileLength << " left = " << left << " -> request was " << count;
            std::string str(result.str());
            log_java(_env, _javaObj, str.c_str());
#endif
            return x;
        }
#ifdef USE_LOG2
        std::stringstream result;
        long left = _fileLength - _cursor;
        result << "close";
        std::string str(result.str());
        log_java(_env, _javaObj, str.c_str());
#endif
        return -1;
    }

    __cdecl int my_seek(void *handle, fluid_long_long_t offset, int origin) {
        if (handle != nullptr && handle == _fileBuf) {
#ifdef USE_LOG2
            std::stringstream result;
            result << "my_seek " << offset << " / " << origin;
            std::string str(result.str());
            log_java(_env, _javaObj, str.c_str());
#endif

            if (origin == SEEK_SET) {
                int x = offset;
                if (x >= 0 && x <= _fileLength) {
                    _cursor = x;
#ifdef USE_LOG2
                    std::stringstream result2;
                    result2 << "now  " << _cursor << " / " << _fileLength;
                    std::string str2(result2.str());
                    log_java(_env, _javaObj, str2.c_str());
#endif
                    return 0;
                }
            } else if (origin == SEEK_CUR) {
                int x = _cursor + offset;
                if (x >= 0 && x <= _fileLength) {
                    _cursor = x;
#ifdef USE_LOG2
                    std::stringstream result2;
                    result2 << "now  " << _cursor << " / " << _fileLength;
                    std::string str2(result2.str());
                    log_java(_env, _javaObj, str2.c_str());
#endif
                    return x >= _fileLength ? -1 : 0;
                }
            } else if (origin == SEEK_END) {
                int x = _fileLength - offset;
                if (x >= 0 && x <= _fileLength) {
                    _cursor = x;
#ifdef USE_LOG2
                    std::stringstream result2;
                    result2 << "now  " << _cursor << " / " << _fileLength;
                    std::string str2(result2.str());
                    log_java(_env, _javaObj, str2.c_str());
#endif
                    return 0;
                }
            }
        }
#ifdef USE_LOG2
        std::stringstream result;
        result << "err";
        std::string str2(result.str());
        log_java(_env, _javaObj, str2.c_str());
#endif
        return -1;
    }

    __cdecl int my_close(void *handle) {
#ifdef USE_LOG
        std::stringstream result;
        result << "my_close" << " prepare " << _cursor;
        std::string str(result.str());
        log_java(_env, _javaObj, str.c_str());
#endif
        if (handle != nullptr && handle == _fileBuf) {
            if (_freeBuf != nullptr) {
                free(_freeBuf);
                _fileLength = 0;
                _cursor = 0;
                _fileBuf = nullptr;
                _freeBuf = nullptr;
            }
        }
#ifdef USE_LOG
        std::stringstream result2;
        result2 << "my_close" << " done " << _cursor;
        std::string str2(result2.str());
        log_java(_env, _javaObj, str2.c_str());
#endif
        return 0;
    }

    __cdecl fluid_long_long_t my_tell(void *handle) {
        if (handle != nullptr && handle == _fileBuf) {
#ifdef USE_LOG2
            std::stringstream result;
            result << "my_tell = " << _cursor;
            std::string str(result.str());
            log_java(_env, _javaObj, str.c_str());
#endif
            return _cursor;
        }
#ifdef USE_LOG2
        std::stringstream result;
        result << "my_tell = -1";
        std::string str(result.str());
        log_java(_env, _javaObj, str.c_str());
#endif
        return -1;
    } ;
}

#endif

typedef struct {
    fluid_settings_t *settings;
    fluid_synth_t *synth;
    fluid_audio_driver_t *audio;

    int soundfont_id;
}  __attribute__ ((aligned(32)))  fluid_handle_t;

#define MAX_SYNTH 10
fluid_handle_t *_allHandle = nullptr;

class MBString {
    std::wstring _w_str;
    std::string _mb_str;

public:
    MBString(JNIEnv *env, jstring &jstr) {
        const jchar *raw = env->GetStringChars(jstr, 0);
        jsize len = env->GetStringLength(jstr);
        _w_str.assign(raw, raw + len);
        env->ReleaseStringChars(jstr, raw);

        std::wstring_convert<std::codecvt_utf8<wchar_t> > converter;

        _mb_str.append(converter.to_bytes(_w_str));
    }

    ~MBString() {
    }

    const wchar_t *w_str() {
        return _w_str.c_str();
    }

    const char *mb_str() {
        return _mb_str.c_str();
    }
};

int findEmptyId() {
    if (_allHandle == nullptr) {
        auto ptr = (unsigned long)malloc(sizeof(fluid_handle_t ) * MAX_SYNTH * 4);
        while ((ptr % 32) != 0) {
            ptr ++;
        }
        _allHandle = (fluid_handle_t *) (void *)ptr;
        if (_allHandle == nullptr) {
            return -1;
        }
        memset(_allHandle, 0, sizeof(fluid_handle_t) * MAX_SYNTH);
    }
    return 0;
    /*
    for (int i = 0; i < MAX_SYNTH; ++i) {
        if (_allHandle[i].settings == nullptr) {
            return i;
        }
    }
    return -1;*/
}
/*
void disposeFluidSynth(int id) {
    if (_allHandle == nullptr) {
        return;
    }
    if (id < 0 || id >= MAX_SYNTH) {
        return;
    }
    fluid_handle_t *handle = _allHandle + id;
    if (handle->synth != nullptr) {
        delete_fluid_synth(handle->synth);
        handle->synth = nullptr;
    }
    if (handle->settings != nullptr) {
        delete_fluid_settings(handle->settings);
        handle->settings = nullptr;
    }
    handle->soundfont_id = 0;
}
*/
fluid_handle_t *getFluid(int handle) {
    if (handle < 0 || handle >= MAX_SYNTH) {
        return nullptr;
    }
    fluid_handle_t *ptr = _allHandle + handle;
    return ptr;
}

extern void initializeMod(fluid_handle_t *fluid);

extern void JNICALL jfluid_close(JNIEnv *env, jobject obj, jint id);

jstring  JNICALL jfluid_list_program(JNIEnv *env, jobject obj, int id) {
    fluid_handle_t *handle = getFluid(id);
    if (handle == nullptr) {
        return env->NewStringUTF("");
    }

    fluid_sfont_t *font = fluid_synth_get_sfont_by_id(handle->synth, handle->soundfont_id);
    int offset = fluid_synth_get_bank_offset(handle->synth, handle->soundfont_id);

    fluid_sfont_iteration_start(font);
    std::stringstream result;
    result << "list program " << id << " addresss " << ((unsigned long) (font)) << "\n";
    char buff[101 + 27];

    while (true) {
        fluid_preset_t *preset = fluid_sfont_iteration_next(font);
        if (preset == nullptr) {
            preset = fluid_sfont_iteration_next(font);
            if (preset == nullptr) {
                preset = fluid_sfont_iteration_next(font);
                if (preset == nullptr) {
                    break;
                }
            }
        }
        int banknum = fluid_preset_get_banknum(preset) + offset;
        int program = fluid_preset_get_num(preset);
        const char *name = fluid_preset_get_name(preset);

        for (int i = 0; i < 100; ++ i) {
            char c = name[i];
            if ((c & 0x80) != 0) {
                c = '_';
            }
            buff[i] = c;
            if (c == 0) {
                break;
            }
        }
        buff[100] = 0;

        result << program;
        result << ",";
        result << banknum;
        result << ",";
        result << "-1";
        result << ",";
        result << buff;
        result << "\n";
    }

    std::string str(result.str());
    char *buf = (char *)str.c_str();
    return env->NewStringUTF(buf);
}


/* calculate cent from key (0 to 11) */
int getKey12Cent(int key) {
    switch (key) {
        case 0:
            return 0;
        case 2:
            return 204;
        case 4:
            return 386;
        case 5:
            return 498;
        case 7:
            return 702;
        case 9:
            return 884;
        case 11:
            return 1088;
    }
    return (getKey12Cent(key - 1) + getKey12Cent(key + 1)) / 2;
}

/* calculate cent from step (0 to 12 + octave offset (1 octave = 1200)) */
double getKeysCent(int step) {
    if (step == 0) {
        return 0;
    }
    double acent = 0;

    while (step < 0) {
        acent -= 1200;
        step += 12;
    }
    while (step >= 12) {
        acent += 1200;
        step -= 12;
    }
    return acent + getKey12Cent(step);
}

void getPitchesJustIntonation(double *pitches, int root) {
    /* root = 0 to 12 */
    for (int key = 0; key < 0x80; ++key) {
        int distance = key - root;
        double cent = getKeysCent(distance);
        pitches[key] = cent;
    }
}

void getPitchesTemperament(double *pitches) {
    for (int key = 0; key < 0x80; ++key) {
        pitches[key] = key * 100;
    }
}

void adjustAmust(double *pitches, float hzamust) {
    /* hzAmust = around 440 to 443 ? */
    auto log2_ratio = log2(hzamust / 440.);
    double amust = 100. * 69 + 1200. * log2_ratio;

    double slide = amust - pitches[69];
    for (int key = 0; key < 0x80; ++key) {
        pitches[key] += slide;
    }
}

int my_fluid_retune(JNIEnv* env, jobject obj, fluid_handle_t *handle, float hzamust, bool equalTemp, int baseKey) {
    int keys[0x80];
    double pitches[0x80];

    fluid_sfont_t *font = fluid_synth_get_sfont_by_id(handle->synth, handle->soundfont_id);

    fluid_sfont_iteration_start(font);
    int ret = FLUID_OK;
    for (int i = 0; i < 0x80; ++i) {
        keys[i] = i;
    }
    if (equalTemp) {
        getPitchesTemperament(pitches);
        adjustAmust(pitches, hzamust);
    } else {
        getPitchesJustIntonation(pitches, baseKey);
        adjustAmust(pitches, hzamust);
    }

    int offset = fluid_synth_get_bank_offset(handle->synth, handle->soundfont_id);
    while (true) {
        fluid_preset_t *preset = fluid_sfont_iteration_next(font);
        if (preset == nullptr) {
            break;
        }
        int bank = fluid_preset_get_banknum(preset) + offset;
        int program = fluid_preset_get_num(preset);

        std::stringstream debugMessage;

#ifdef USE_LOG
        debugMessage.str("");
        debugMessage.clear();
        debugMessage << "bank:" << bank << ", prog:" << program;
        std::string  str2(debugMessage.str());
        log_java(env, obj, str2.c_str());
#endif
        if (bank < 0 || bank >= 128 || program < 0 || program >= 128) {
            continue;
        }

        /*
        if (equalTemp && hzamust== 440.) {
            for (int ch = 0; ch < 16; ++ ch) {
                fluid_synth_activate_tuning(handle->synth, ch, bank, program, 0);
            }
            continue;
        }*/

        int code = fluid_synth_tune_notes(handle->synth, bank, program, 0x80, keys, pitches, 1);
        if (code == FLUID_OK) {
            for (int ch = 0; ch < 16; ++ch) {
                code = fluid_synth_activate_tuning(handle->synth, ch, bank, program, 1);
            }
        }
        if (code != FLUID_OK) {
            ret = code;
        }
    }
    return ret;
}

void JNICALL
jfluid_retune(JNIEnv *env, jobject obj, jint id, jfloat hzamust, jboolean equalTemp, int baseKey) {
    fluid_handle_t *handle = getFluid(id);
    if (handle == nullptr) {
        return;
    }

    my_fluid_retune(env, obj, handle, hzamust, equalTemp >= 1, baseKey);
}

jboolean JNICALL jfluid_is_soundfont(JNIEnv *env, jobject obj, jstring fontFile) {
    MBString file(env, fontFile);
    if (fluid_is_soundfont(file.mb_str())) {
        return JNI_TRUE;
    }
    else {
        return JNI_FALSE;
    }
}

fluid_sfloader_t *my_sfloader = nullptr;

jint JNICALL jfluid_open(JNIEnv *env, jobject obj, jstring fontFile, jboolean lowlatency) {
    int id = findEmptyId();

    if (obj != nullptr && obj != _javaObj) {
        _javaObj = obj;
    }
#ifdef USE_LOG
    std::stringstream debugMessage;
    debugMessage.str("");
    debugMessage.clear();
    debugMessage << "emptID =" << id;
    std::string  str2(debugMessage.str());
    log_java(env, obj, str2.c_str());
#endif
    if (id < 0) {
        return id;
    }

    fluid_handle_t *handle = getFluid(id);
#ifdef USE_LOG
    debugMessage.str("");
    debugMessage.clear();
    debugMessage << "handle =" << handle;
    std::string  str3(debugMessage.str());
    log_java(env, obj, str3.c_str());
#endif
    if (handle == nullptr) {
        return -1;
    }
    if (handle->audio != nullptr) {
        delete_fluid_audio_driver(handle->audio);
        handle->audio = nullptr;
        std::this_thread::sleep_for(std::chrono::milliseconds(400));
    }

    if (handle->settings == nullptr) {
        handle->settings = new_fluid_settings();

        fluid_settings_setstr(handle->settings, "synth.audio-driver", "oboe");
        fluid_settings_setstr(handle->settings, "audio.oboe.sample-rate-conversion-quality",
                              "Fastest");
        if (lowlatency) {
            fluid_settings_setstr(handle->settings, "audio.oboe.sharing-mode", "Exclusive");
            fluid_settings_setint(handle->settings, "audio.periods", 4);
            fluid_settings_setint(handle->settings, "audio.period-size", 64);
            fluid_settings_setstr(handle->settings, "audio.oboe.performance-mode", "LowLatency");
        } else {
            fluid_settings_setstr(handle->settings, "audio.oboe.sharing-mode", "Shared");
            fluid_settings_setint(handle->settings, "audio.periods", 16);
            fluid_settings_setint(handle->settings, "audio.period-size", 64);
            fluid_settings_setstr(handle->settings, "audio.oboe.performance-mode", "None");
        }
        fluid_settings_setint(handle->settings, "synth.audio-channels", 2);
        fluid_settings_setint(handle->settings, "synth.cpu-cores", 4);
        fluid_settings_setint(handle->settings, "synth.sample-rate", 48000);
        //fluid_settings_setint(handle->settings, "audio.realtime-prio", 60);
        //fluid_settings_setstr(handle->settings, "player.timing-source", "sample");

    }
#ifdef USE_LOG
    debugMessage.str("");
    debugMessage.clear();
    debugMessage << "setting=" << handle->settings;
    std::string  str4(debugMessage.str());
    log_java(env, obj, str4.c_str());
#endif

    if (handle->synth == nullptr) {
        handle->synth = new_fluid_synth(handle->settings);
        if (my_sfloader == nullptr) {
#if HANDLE_FILE
            my_sfloader = new_fluid_defsfloader(handle->settings);
            int x = fluid_sfloader_set_callbacks(my_sfloader,
                                                 my_open,
                                                 my_read,
                                                 my_seek,
                                                 my_tell,
                                                 my_close);
            fluid_synth_add_sfloader(handle->synth, my_sfloader);
#endif
        }
    }

#ifdef USE_LOG

    debugMessage.str("");
    debugMessage.clear();
    debugMessage << "synth=" << handle->synth;
    std::string  str5(debugMessage.str());
    log_java(env, obj, str5.c_str());

#endif
    if (handle->synth == nullptr) {
        //jfluid_close(env, obj, id);
        return -1;
    }

    //fluid_synth_set_polyphony(handle->synth, 128);
    //fluid_synth_set_interp_method(handle->synth, -1, FLUID_INTERP_LINEAR);

#ifdef USE_LOG
    debugMessage.str("");
    debugMessage.clear();
    debugMessage << "mod ok 1 ";
    std::string str60(debugMessage.str());
    log_java(env, obj, str60.c_str());
#endif
    while (true) {
        int count = fluid_synth_sfcount(handle->synth);
        if (count == 0) {
            break;
        }
        fluid_sfont_t *font = fluid_synth_get_sfont(handle->synth, count - 1);
#ifdef USE_LOG
        debugMessage.clear();
        debugMessage << "mod ok 2 " << (unsigned long)font ;
        std::string str61(debugMessage.str());
        log_java(env, obj, str61.c_str());
#endif

        if (font == nullptr) {
            break;
        }
        fluid_synth_remove_sfont(handle->synth, font);

    }
    if (handle->soundfont_id >= 0) {
        fluid_synth_sfunload(handle->synth, handle->soundfont_id, 0);
        handle->soundfont_id = -1;
    }
#ifdef USE_LOG
    debugMessage.str("");
    debugMessage.clear();
    debugMessage << "mod ok 2";
    std::string str61(debugMessage.str());
    log_java(env, obj, str61.c_str());
#endif
#ifdef USE_LOG
    debugMessage.str("");
    debugMessage.clear();
    debugMessage << "mod ok 3 ";
    std::string str63(debugMessage.str());
    log_java(env, obj, str63.c_str());
#endif
    MBString file(env, fontFile);
    if (fluid_is_soundfont(file.mb_str())) {
#ifdef USE_LOG
        debugMessage.str("");
        debugMessage.clear();
        debugMessage << "mod ok 4 ";
        std::string str64(debugMessage.str());
        log_java(env, obj, str64.c_str());

        std::string str641(file.mb_str());
        log_java(env, obj, str641.c_str());
#endif

#ifdef USE_LOG
        debugMessage.str("");
        debugMessage.clear();
        debugMessage << "mod ok 4.5 ";
        std::string str642(debugMessage.str());
        log_java(env, obj, str642.c_str());

        std::string str643(file.mb_str());
        log_java(env, obj, str643.c_str());
#endif
        handle->soundfont_id = fluid_synth_sfload(handle->synth, file.mb_str(), 0);
    } else {
        return -1;
    }


#ifdef USE_LOG
    debugMessage.str("");
    debugMessage.clear();
    debugMessage << "mod ok 5 ";
    debugMessage << "soundFont=" << handle->soundfont_id;
    std::string str65(debugMessage.str());
    log_java(env, obj, str65.c_str());
#endif

    if (handle->soundfont_id == FLUID_FAILED) {
        //jfluid_close(env, obj, id);
        return -1;
    }


    if (handle->audio == nullptr) {
        handle->audio = new_fluid_audio_driver(handle->settings, handle->synth);
        initializeMod(handle);
    }

#ifdef USE_LOG
    debugMessage.str("");
    debugMessage.clear();
    debugMessage << "audio=" << handle->audio;
    std::string  str8(debugMessage.str());
    log_java(env, obj, str8.c_str());
#endif

    return id;
}


void JNICALL jfluid_close(JNIEnv *env, jobject obj, jint id) {
        /*
    }
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr) {
        if (handle->soundfont_id != 0) {
            fluid_synth_sfunload(handle->synth, handle->soundfont_id, 1);
            handle->soundfont_id = -1;
        }
        if (handle->synth != nullptr) {
            delete_fluid_synth(handle->synth);
            handle->synth = nullptr;
        }
        if (handle->audio != nullptr) {
            delete_fluid_audio_driver(handle->audio);
            handle->audio = nullptr;
        }
        if (handle->settings != nullptr) {
            delete_fluid_settings(handle->settings);
            handle->settings = nullptr;
        }
    }*/
}


#define SOUND_CTRL_FILTER_RESONANCE 71
#define SOUND_CTRL_RELEASE 72
#define SOUND_CTRL_ATTACK 73
#define SOUND_CTRL_CUTOFF 74
#define SOUND_CTRL_DECAY 75
#define SOUND_CTRL_SUSTAIN 79

#define SOUND_EFFECT_REVERVE 91
#define SOUND_EFFECT_TREMOLO 92
#define SOUND_EFFECT_CHORUS 93
#define SOUND_EFFECT_DETUNE 94
#define SOUND_EFFECT_PHASER 95

void reset_control(fluid_handle_t* handle, int ch) {
    fluid_synth_cc(handle->synth, ch, 10, 64); //panpot
    fluid_synth_cc(handle->synth, ch, SOUND_CTRL_FILTER_RESONANCE, 0);
    fluid_synth_cc(handle->synth, ch, SOUND_CTRL_RELEASE, 64);
    fluid_synth_cc(handle->synth, ch, SOUND_CTRL_ATTACK, 64);
    fluid_synth_cc(handle->synth, ch, SOUND_CTRL_CUTOFF, 64);
    fluid_synth_cc(handle->synth, ch, SOUND_CTRL_DECAY, 64);
    fluid_synth_cc(handle->synth, ch, SOUND_CTRL_SUSTAIN, 64);

    fluid_synth_cc(handle->synth, ch, SOUND_EFFECT_REVERVE, 30);
    fluid_synth_cc(handle->synth, ch, SOUND_EFFECT_TREMOLO, 0);
    fluid_synth_cc(handle->synth, ch, SOUND_EFFECT_CHORUS, 0);
    fluid_synth_cc(handle->synth, ch, SOUND_EFFECT_DETUNE, 0);
    fluid_synth_cc(handle->synth, ch, SOUND_EFFECT_PHASER, 0);
    fluid_synth_set_portamento_mode(handle->synth, ch, FLUID_CHANNEL_PORTAMENTO_MODE_EACH_NOTE);
}

void initializeMod(fluid_handle_t* handle) {

    fluid_synth_set_gain(handle->synth, 0.7);
    // soundfont spec says that if cutoff is >20kHz and resonance Q is 0, then no filtering occurs

    fluid_mod_t *mod;
    /*
    mod  = new_fluid_mod();

    fluid_mod_set_source1(mod,
                          SOUND_EFFECT_REVERVE, //reverve
                          FLUID_MOD_CC
                          | FLUID_MOD_LINEAR
                          | FLUID_MOD_UNIPOLAR
                          | FLUID_MOD_POSITIVE);
    fluid_mod_set_source2(mod, 0, 0);
    fluid_mod_set_dest(mod, GEN_REVERBSEND);
    fluid_mod_set_amount(mod, 200);
    fluid_synth_add_default_mod(handle->synth, mod, FLUID_SYNTH_ADD);

    mod  = new_fluid_mod();

    fluid_mod_set_source1(mod,
                          SOUND_EFFECT_CHORUS, //chorus
                          FLUID_MOD_CC
                          | FLUID_MOD_LINEAR
                          | FLUID_MOD_UNIPOLAR
                          | FLUID_MOD_POSITIVE);
    fluid_mod_set_source2(mod, 0, 0);
    fluid_mod_set_dest(mod, GEN_CHORUSSEND);
    fluid_mod_set_amount(mod, 1000);
    fluid_synth_add_default_mod(handle->synth, mod, FLUID_SYNTH_ADD);

     */
    // http://www.synthfont.com/SoundFont_NRPNs.PDF
/*
    mod = new_fluid_mod();
    fluid_mod_set_source1(mod,
                          SOUND_CTRL_FILTER_RESONANCE,
                          FLUID_MOD_CC
                          | FLUID_MOD_UNIPOLAR
//                            | FLUID_MOD_LINEAR
                          | FLUID_MOD_CONCAVE
                          | FLUID_MOD_POSITIVE);
    fluid_mod_set_source2(mod, 0, 0);
    fluid_mod_set_dest(mod, GEN_FILTERQ);
    fluid_mod_set_amount(mod, FLUID_PEAK_ATTENUATION);
    fluid_synth_add_default_mod(handle->synth, mod, FLUID_SYNTH_OVERWRITE);
    mod = new_fluid_mod();

    fluid_mod_set_source1(mod,
                          SOUND_CTRL_RELEASE, // MIDI CC 72 Release time
                          FLUID_MOD_CC
                          | FLUID_MOD_BIPOLAR
                          | FLUID_MOD_LINEAR
                          | FLUID_MOD_POSITIVE);
    fluid_mod_set_source2(mod, 0, 0);
    fluid_mod_set_dest(mod, GEN_VOLENVRELEASE);
    fluid_mod_set_amount(mod, 12000);
    fluid_synth_add_default_mod(handle->synth, mod, FLUID_SYNTH_ADD);

    mod = new_fluid_mod();

    float env_amount = 20000.0f;
    fluid_mod_set_source1(mod,
                          SOUND_CTRL_ATTACK, // MIDI CC 73 Attack time
                          FLUID_MOD_CC
                          | FLUID_MOD_BIPOLAR
                          | FLUID_MOD_LINEAR
                          | FLUID_MOD_POSITIVE);
    fluid_mod_set_source2(mod, 0, 0);
    fluid_mod_set_dest(mod, GEN_VOLENVATTACK);
    fluid_mod_set_amount(mod, env_amount);
    fluid_synth_add_default_mod(handle->synth, mod, FLUID_SYNTH_ADD);
    // soundfont spec says that if cutoff is >20kHz and resonance Q is 0, then no filtering occurs
    mod = new_fluid_mod();

    fluid_mod_set_source1(mod,
                          SOUND_CTRL_CUTOFF, // MIDI CC 74 Brightness (cutoff frequency, FILTERFC)
                          FLUID_MOD_CC
                          | FLUID_MOD_LINEAR
                          | FLUID_MOD_BIPOLAR
                          | FLUID_MOD_POSITIVE);
    fluid_mod_set_source2(mod, 0, 0);
    fluid_mod_set_dest(mod, GEN_FILTERFC);
    fluid_mod_set_amount(mod, 10000.0f);
    fluid_synth_add_default_mod(handle->synth, mod, FLUID_SYNTH_ADD);

    mod = new_fluid_mod();

    fluid_mod_set_source1(mod,
                          SOUND_CTRL_DECAY, // MIDI CC 75 Decay Time
                          FLUID_MOD_CC
                          | FLUID_MOD_UNIPOLAR
                          | FLUID_MOD_LINEAR
                          | FLUID_MOD_POSITIVE);
    fluid_mod_set_source2(mod, 0, 0);
    fluid_mod_set_dest(mod, GEN_VOLENVDECAY);
    fluid_mod_set_amount(mod, env_amount);
    fluid_synth_add_default_mod(handle->synth, mod, FLUID_SYNTH_ADD);

    mod = new_fluid_mod();

    fluid_mod_set_source1(mod,
                          SOUND_CTRL_SUSTAIN, // MIDI CC 79 undefined
                          FLUID_MOD_CC
                          | FLUID_MOD_UNIPOLAR
                          | FLUID_MOD_CONCAVE
                          | FLUID_MOD_NEGATIVE);
    fluid_mod_set_source2(mod, 0, 0);
    fluid_mod_set_dest(mod, GEN_VOLENVSUSTAIN);

    // fluice_voice.c#fluid_voice_update_param()
    // clamps the range to between 0 and 1000, so we'll copy that
    fluid_mod_set_amount(mod, 1000.0f);
    fluid_synth_add_default_mod(handle->synth, mod, FLUID_SYNTH_ADD);

    for (int ch = 0; ch < 16; ++ch) {
        reset_control(handle, ch);
    }
*/
}

fluid_midi_event_t *_event = nullptr;

int gmReset[] = {0xF0, 0x7e, 0x7f, 0x09, 0x01, 0xf7};
int gsReset[] = {0xF0, 0x41, -1, 0x42, 0x12, 0x40, 0x00, 0x7F, 0x00, 0x41, 0xF7};
int xgReset[] = {0xF0, 0x43, -1, 0x4C, 0x00, 0x00, 0x7E, 0x00, 0xF7};
int masterVolume[] = {0xF0, 0x7f, 0x7F, 0x04, 0x01, 0x11, -1, 0xF7};
int masterVolume_pos = 6;

bool matchBytes(int *temp, jbyte *data, int dataLen) {
    for (int i = 0; i < dataLen; ++i) {
        int c = data[i] & 0xff;
        int x = temp[i];
        if (x == 0xf7) {
            if (c == 0xf7) {
                return true;
            } else {
                return false;
            }
        }
        if (x != c) {
            if (x < 0) {
                continue;
            } else {
                return false;
            }
        }
    }
    return false;
}

extern void JNICALL
jfluid_short_message(JNIEnv *env, jobject obj, jint id, jint status, jint data1, jint data2);

void JNICALL jfluid_long_message(JNIEnv *env, jobject obj, jint id, jbyteArray data) {
    jsize len = env->GetArrayLength(data);
    jbyte *b = env->GetByteArrayElements(data, 0);
    if (len <= 3) {
        int status = 0;
        int data1 = 0;
        int data2 = 0;
        if (len >= 1) {
            status = b[0] & 0xff;
        }
        if (len >= 2) {
            data1 = b[1] & 0xff;
        }
        if (len >= 3) {
            data2 = b[2] & 0xff;
        }
        jfluid_short_message(env, obj, id, status, data1, data2);
    } else {
        if (matchBytes(gmReset, b, len) || matchBytes(gsReset, b, len) ||
            matchBytes(xgReset, b, len)) {
            fluid_handle_t *handle = getFluid(id);
            if (handle != nullptr) {
                fluid_synth_system_reset(handle->synth);
                fluid_synth_program_reset(handle->synth);
            }
        } else if (matchBytes(masterVolume, b, len)) {
            fluid_handle_t *handle = getFluid(id);
            if (handle != nullptr) {
                int c = b[masterVolume_pos] & 0x0ff;
                fluid_synth_set_gain(handle->synth, 0.7 * c / 127);
            }
        }
    }
    env->ReleaseByteArrayElements(data, b, JNI_ABORT);
}

void JNICALL
jfluid_short_message(JNIEnv *env, jobject obj, jint id, jint status, jint data1, jint data2) {
    fluid_handle_t *handle = getFluid(id);
    if (_event == nullptr) {
        _event = new_fluid_midi_event();
    }

    if (handle != nullptr && handle->synth != nullptr) {
        int command = status & 0xf0;
        int ch = status & 0x0f;

        if (command == 0x80) { //noteoff
            fluid_synth_noteoff(handle->synth, ch, data1);
            return;
        }
        if (command == 0x90) {//noteon
            if (data2 == 0) {
                fluid_synth_noteoff(handle->synth, ch, data1);
                return;
            } else {
                fluid_synth_noteon(handle->synth, ch, data1, data2);
                return;
            }
        }
        if (command == 0xa0) { //polypressure
            fluid_synth_key_pressure(handle->synth, ch, data1, data2);
            return;
        }
        if (command == 0xb0) { //CC
            if (data1 == 0) {
                data2 &= 0x0ff;

                if (data2 >= 0x78) {
                    fluid_synth_set_channel_type(handle->synth, ch, CHANNEL_TYPE_DRUM);
                }else if (ch == 9 && data2 == 0) {
                    data2 = 0x7f;
                    fluid_synth_set_channel_type(handle->synth, ch, CHANNEL_TYPE_DRUM);
                }else {
                    fluid_synth_set_channel_type(handle->synth, ch, CHANNEL_TYPE_MELODIC);
                }
                fluid_synth_bank_select(handle->synth, ch, data2);
                fluid_synth_program_reset(handle->synth);
            } else {
                fluid_synth_cc(handle->synth, ch, data1, data2);
                if (data1 == 121) {
                    reset_control(handle, ch);
                }
            }
            return;
        }
        if (command == 0xc0) { //program change
            fluid_synth_program_change(handle->synth, ch, data1);
            return;
        }
        if (command == 0xd0) {
            fluid_synth_channel_pressure(handle->synth, ch, data1);
            return;
        }
        if (command == 0xe0) { //pitch
            fluid_synth_pitch_bend(handle->synth, ch, (data2 << 7) | data1);
            return;
        }
    }
}
/*

void JNICALL jfluid_set_double(JNIEnv *env, jobject obj, jint id, jstring key, jdouble value) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        MBString jkey(env, key);
        fluid_settings_setnum(handle->settings, jkey.mb_str(), (float) value);
    }
}

void JNICALL jfluid_set_int(JNIEnv *env, jobject obj, jint id, jstring key, jint value) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        MBString jkey(env, key);
        fluid_settings_setint(handle->settings, jkey.mb_str(), (int) value);
    }
}

void JNICALL jfluid_set_string(JNIEnv *env, jobject obj, jint id, jstring key, jstring value) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        MBString jkey(env, key);
        MBString jvalue(env, value);
        fluid_settings_setstr(handle->settings, jkey.mb_str(), jvalue.mb_str());
    }
}

void JNICALL fluid_get_double(JNIEnv *env, jobject obj, jint id, jstring key, jobject ref) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        jclass cl = env->GetObjectClass(ref);
        jmethodID mid = env->GetMethodID(cl, "setValue", "(D)V");
        if (mid != 0) {
            double value = 0;
            MBString jkey(env, key);

            fluid_settings_getnum(handle->settings, jkey.mb_str(), &value);

            env->CallVoidMethod(ref, mid, (jdouble) value);
        }
    }
}

void JNICALL jfluid_get_int(JNIEnv *env, jobject obj, jint id, jstring key, jobject ref) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        jclass cl = env->GetObjectClass(ref);
        jmethodID mid = env->GetMethodID(cl, "setValue", "(I)V");
        if (mid != 0) {
            int value = 0;
            MBString jkey(env, key);

            fluid_settings_getint(handle->settings, jkey.mb_str(), &value);
            env->CallVoidMethod(ref, mid, (jint) value);
        }
    }
}

void JNICALL jfluid_get_string(JNIEnv *env, jobject obj, jint id, jstring key, jobject ref) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        jclass cl = env->GetObjectClass(ref);
        jmethodID mid = env->GetMethodID(cl, "setValue", "(Ljava/lang/String;)V");
        if (mid != 0) {
            jstring jvalue = nullptr;
            char *value = nullptr;
            MBString jkey(env, key);

            fluid_settings_dupstr(handle->settings, jkey.mb_str(), &value);
            jvalue = env->NewStringUTF(value);

            env->CallVoidMethod(ref, mid, jvalue);
        }
    }
}

void JNICALL
jfluid_get_default_double(JNIEnv *env, jobject obj, jint id, jstring key, jobject ref) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        jclass cl = env->GetObjectClass(ref);
        jmethodID mid = env->GetMethodID(cl, "setValue", "(D)V");
        if (mid != 0) {
            MBString jkey(env, key);
            double value = 0;
            fluid_settings_getnum_default(handle->settings, jkey.mb_str(), &value);

            env->CallVoidMethod(ref, mid, (jdouble) value);
        }
    }
}

void JNICALL jfluid_get_default_int(JNIEnv *env, jobject obj, jint id, jstring key, jobject ref) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        jclass cl = env->GetObjectClass(ref);
        jmethodID mid = env->GetMethodID(cl, "setValue", "(I)V");
        if (mid != 0) {
            MBString jkey(env, key);
            int value = 0;
            fluid_settings_getint_default(handle->settings, jkey.mb_str(), &value);

            env->CallVoidMethod(ref, mid, (jint) value);
        }
    }
}

void JNICALL
jfluid_get_default_string(JNIEnv *env, jobject obj, jint id, jstring key, jobject ref) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        jclass cl = env->GetObjectClass(ref);
        jmethodID mid = env->GetMethodID(cl, "setValue", "(Ljava/lang/String;)V");
        if (mid != 0) {
            MBString jkey(env, key);
            char *value;
            fluid_settings_getstr_default(handle->settings, jkey.mb_str(), &value);
            jstring jvalue = env->NewStringUTF(value);
            env->CallVoidMethod(ref, mid, jvalue);
        }
    }
}

void JNICALL
jfluid_get_double_range(JNIEnv *env, jobject obj, jint id, jstring key, jobject minimumRef,
                        jobject maximumRef) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        jclass clMin = env->GetObjectClass(minimumRef);
        jclass clMax = env->GetObjectClass(maximumRef);
        jmethodID midMin = env->GetMethodID(clMin, "setValue", "(D)V");
        jmethodID midMax = env->GetMethodID(clMax, "setValue", "(D)V");
        if (midMin != 0 && midMax != 0) {
            double minimum = 0;
            double maximum = 0;
            MBString jkey(env, key);

            fluid_settings_getnum_range(handle->settings, jkey.mb_str(), &minimum, &maximum);
            env->CallVoidMethod(minimumRef, midMin, (jdouble) minimum);
            env->CallVoidMethod(maximumRef, midMax, (jdouble) maximum);
        }
    }
}

void JNICALL
jfluid_get_int_range(JNIEnv *env, jobject obj, jint id, jstring key, jobject minimumRef,
                     jobject maximumRef) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        jclass clMin = env->GetObjectClass(minimumRef);
        jclass clMax = env->GetObjectClass(maximumRef);
        jmethodID midMin = env->GetMethodID(clMin, "setValue", "(I)V");
        jmethodID midMax = env->GetMethodID(clMax, "setValue", "(I)V");
        if (midMin != 0 && midMax != 0) {
            int minimum = 0;
            int maximum = 0;
            MBString jkey(env, key);

            fluid_settings_getint_range(handle->settings, jkey.mb_str(), &minimum, &maximum);
            env->CallVoidMethod(minimumRef, midMin, (jint) minimum);
            env->CallVoidMethod(maximumRef, midMax, (jint) maximum);
        }
    }
}


typedef struct {
    JNIEnv *env;
    jobject options;
} fluid_settings_foreach_option_data;

void fluid_settings_foreach_option_callback(void *data, char *name, char *option) {
    fluid_settings_foreach_option_data *handle = (fluid_settings_foreach_option_data *) data;

    jstring joption = (handle->env)->NewStringUTF(option);
    jclass cl = (handle->env)->GetObjectClass(handle->options);
    jmethodID mid = (handle->env)->GetMethodID(cl, "add", "(Ljava/lang/Object;)Z");
    if (mid != 0) {
        (handle->env)->CallBooleanMethod(handle->options, mid, joption);
    }
}


void JNICALL
jfluid_get_properties(JNIEnv *env, jobject obj, jint id, jstring key, jobject options) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr) {
        MBString jkey(env, key);
        fluid_settings_foreach_option_data *data = (fluid_settings_foreach_option_data *) malloc(
                sizeof(fluid_settings_foreach_option_data));
        data->env = env;
        data->options = options;

        fluid_settings_foreach_option(handle->settings, jkey.mb_str(), data,
                                      (fluid_settings_foreach_option_t) fluid_settings_foreach_option_callback);

        free(data);
    }
}

void JNICALL
jfluid_is_realtime_property(JNIEnv *env, jobject obj, jint id, jstring key, jobject ref) {
    fluid_handle_t *handle = getFluid(id);
    if (handle != nullptr && handle->settings != nullptr && key != nullptr) {
        jclass cl = env->GetObjectClass(ref);
        jmethodID mid = env->GetMethodID(cl, "setValue", "(Z)V");
        if (mid != 0) {
            MBString jkey(env, key);
            int value = fluid_settings_is_realtime(handle->settings, jkey.mb_str());
            env->CallVoidMethod(ref, mid, (value != 0 ? JNI_TRUE : JNI_FALSE));
        }
    }
}

*/

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    _javaVM = vm;
    _env = env;

    // Find your class. JNI_OnLoad is called from the correct class loader context for this to work.
    jclass c = env->FindClass("fsexample/fsexample/midione/drivers/fluid/JFluid");
    if (c == nullptr) return JNI_ERR;

    static JNINativeMethod methods[] = {
            {(char *) "open",             (char *) "(Ljava/lang/String;Z)I", reinterpret_cast<void *>(jfluid_open)},
            {(char *) "isSoundFont",      (char *) "(Ljava/lang/String;)Z",  reinterpret_cast<void *>(jfluid_is_soundfont)},
            {(char *) "retune",           (char *) "(IFZI)V",                reinterpret_cast<void *>(jfluid_retune)},
            {(char *) "close",            (char *) "(I)V",                   reinterpret_cast<void *>(jfluid_close)},
            {(char *) "listProgram",      (char *) "(I)Ljava/lang/String;",  reinterpret_cast<void *>(jfluid_list_program)},
            {(char *) "sendShortMessage", (char *) "(IIII)V",                reinterpret_cast<void *>(jfluid_short_message)},
            {(char *) "sendLongMessage",  (char *) "(I[B)V",                 reinterpret_cast<void *>(jfluid_long_message)},
            /*
            void JNICALL jfluid_set_double(JNIEnv* env, jobject obj, jint id, jstring key, jdouble value)
            void JNICALL jfluid_set_int(JNIEnv* env, jobject obj, jint id, jstring key, jint value)
            void JNICALL jfluid_set_string(JNIEnv* env, jobject obj, jint id, jstring key, jstring value)
            void JNICALL fluid_get_double(JNIEnv* env, jobject obj, jint id, jstring key, jobject ref)
            void JNICALL jfluid_get_int(JNIEnv* env, jobject obj, jint id, jstring key, jobject ref)
            void JNICALL jfluid_get_string(JNIEnv* env, jobject obj, jint id, jstring key, jobject ref)
            void JNICALL jfluid_get_default_double(JNIEnv* env, jobject obj, jint id, jstring key, jobject ref)
            void JNICALL jfluid_get_default_int(JNIEnv* env, jobject obj, jint id, jstring key, jobject ref)
            void JNICALL jfluid_get_default_string(JNIEnv* env, jobject obj, jint id, jstring key, jobject ref)
            void JNICALL jfluid_get_double_range(JNIEnv* env, jobject obj, jint id, jstring key, jobject minimumRef, jobject maximumRef)
            void JNICALL jfluid_get_int_range(JNIEnv* env, jobject obj, jint id, jstring key, jobject minimumRef, jobject maximumRef)
            void JNICALL jfluid_get_properties(JNIEnv* env, jobject obj, jint id, jstring key, jobject options)
            void JNICALL jfluid_is_realtime_property(JNIEnv* env, jobject obj, jint id, jstring key, jobject ref)
            */
    };
    int rc = env->RegisterNatives(c, methods, sizeof(methods) / sizeof(JNINativeMethod));
    if (rc != JNI_OK) return rc;
    _javaClass = c;
    _env = env;
    _logging = env->GetMethodID(c, (char *) "log", "(Ljava/lang/String;)V");

    return JNI_VERSION_1_6;
}
