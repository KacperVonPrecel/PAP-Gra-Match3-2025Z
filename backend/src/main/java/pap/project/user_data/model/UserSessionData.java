package pap.project.user_data.model;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class UserSessionData
{
    private @NonNull final ReentrantLock lock;
    private @Nullable UserData userData;

    public UserSessionData()
    {
        this.lock = new ReentrantLock();
        this.userData = null;
    }

    /**
     * It can be called without problems multiple times in one thread without calling {@link #unlock()} before it,
     * but it's required to call {@link #unlock()} the same times these method was called after it.
     * For more information look {@link ReentrantLock}.
     */
    public void lock()
    {
        lock.lock();
    }

    /**
     * For more information look {@link #unlock()}
     */
    public boolean lockWithTimeout(long milliseconds)
    {
        try
        {
            return lock.tryLock(milliseconds, TimeUnit.MILLISECONDS);
        } catch (InterruptedException wyj)
        {
            Thread.interrupted();
            return false;
        }

    }

    public void unlock()
    {
        lock.unlock();
    }

    public void setUserData(@NonNull UserData userData)
    {
        this.userData = userData;
    }

    public @Nullable UserData getUserData()
    {
        return userData;
    }
}
