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

    public void lock()
    {
        lock.lock();
    }

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
