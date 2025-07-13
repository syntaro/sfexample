package fsexample.fsexample.midione.v1;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class OneDevice {
    public interface StatListener {
        void onStatChange(Stat stat);
    }

    static LinkedList<StatListener> _statListener = new LinkedList<>();

    public static synchronized void addStatListener(StatListener listener) {
        for (StatListener seek : _statListener) {
            if (seek == listener) {
                return;
            }
        }
        _statListener.add(listener);
    }

    public static synchronized void removeStatListener(StatListener listener) {
        _statListener.remove(listener);
    }

    static synchronized void notifyStat(Stat stat) {
        ArrayList<StatListener> copy;
        synchronized (OneDevice.class) {
            copy = new ArrayList<>(_statListener);
        }
        for (StatListener seek : copy) {
            seek.onStatChange(stat);
        }
    }

    public class Stat {
        public final OneDevice _itis;
        public int _openError = 0;
        public int _transferBroken = 0;

        public Stat() {
            _itis = OneDevice.this;
        }

        public void notifyStat() {
            OneDevice.notifyStat(this);
        }
    }

    public final Stat _stat = new Stat();

    public OneDevice(@NonNull OneDriver driver, @NonNull String name, @NonNull String uuid) {
        _driver = driver;
        _name = name;
        if (name == null) {
            throw new NullPointerException();
        }
        _uuid = uuid;
    }

    public OneDriver getDriver() {
        return _driver;
    }

    public String _name;
    public OneDriver _driver;
    public String _uuid;

    public String getName() {
        return _name;
    }

    public void closeDevice() {
        onDispose();
        if (_listInput != null) {
            for (OneInput seek : _listInput) {
                if (seek != null) {
                    seek.onClose();
                }
            }
        }
        if (_listOutput != null) {
            for (OneOutput seek : _listOutput) {
                if (seek != null) {
                    seek.onClose();
                }
            }
        }
        _listOutput = null;
        _listInput = null;
    }

    private OneOutput[] _listOutput = null;
    private OneInput[] _listInput = null;

    public synchronized OneOutput getOutput(int track) {
        if (_listOutput == null) {
            _listOutput = new OneOutput[countOutput()];
        }
        if (_listOutput[track] == null) {
            OneOutput out = allocateOutput(track);
            _listOutput[track] = out;
        }
        return _listOutput[track];
    }

    public synchronized OneInput getInput(int track) {
        if (_listInput == null) {
            _listInput = new OneInput[countInput()];
        }
        if (_listInput[track] == null) {
            OneInput in = allocateInput(track);
            _listInput[track] = in;
        }
        return _listInput[track];
    }

    public String toString() {
        return _name;
    }

    public abstract int getSortOrder();

    public abstract void startAccessDevice();

    protected abstract void onDispose();

    public abstract int countOutput();

    public abstract int countInput();

    public abstract OneOutput allocateOutput(int track);

    public abstract OneInput allocateInput(int track);

    OneEventCounter _counter = new OneEventCounter(this);

    public OneEventCounter getEventCounter() {
        return _counter;
    }
}
