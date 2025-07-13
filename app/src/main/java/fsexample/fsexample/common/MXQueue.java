package fsexample.fsexample.common;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * @author Syntarou YOSHIDA
 */
public class MXQueue<T> {

    LinkedList<T> _queue;
    boolean _pausing;

    public MXQueue() {
        _queue = new LinkedList<T>();
        _pausing = false;
    }

    public synchronized void push(T obj) {
        _queue.add(obj);
        notifyAll();
    }

    public synchronized boolean pushIfNotLast(T obj) {
        if (_queue.isEmpty() || _queue.getLast() != obj) {
            _queue.add(obj);
            notifyAll();
            return true;
        }
        return false;
    }

    public synchronized void push(T obj, Comparator<T> comp) {
        if (_queue.isEmpty()) {
            _queue.add(obj);
        } else {
            T last = _queue.getLast();
            if (comp.compare(last, obj) < 0) {
                _queue.add(obj);
            } else {
                int size = _queue.size();
                int index = size - 1;
                while (index > 0 && comp.compare(_queue.get(index - 1), obj) > 0) {
                    index--;
                }
                _queue.add(index, obj);
            }
        }
        notifyAll();
    }

    public synchronized boolean isEmpty() {
        return _queue.isEmpty();
    }

    public synchronized int size() {
        return _queue.size();
    }

    public synchronized T popAndNoRemove() {
        while (true) {
            while (_queue.isEmpty() && !_pausing) {
                try {
                    wait(3000);
                } catch (InterruptedException ex) {
                    return null;
                }
            }
            if (_pausing) {
                return null;
            }
            if (!_queue.isEmpty()) {
                return _queue.peekFirst();
            }
        }
    }

    public synchronized T pop() {
        while (true) {
            while (_queue.isEmpty() && !_pausing) {
                try {
                    wait(5000);
                } catch (InterruptedException ex) {
                    _pausing = true;
                    return null;
                }
            }
            if (_pausing) {
                return null;
            }
            if (!_queue.isEmpty()) {
                return _queue.removeFirst();
            }
        }
    }

    public synchronized void back(T item) {
        _queue.add(0, item);
        notifyAll();
    }

    public synchronized void pause() {
        _pausing = true;
        notifyAll();
    }

    public synchronized void resume() {
        _pausing = false;
        notifyAll();
    }
}
