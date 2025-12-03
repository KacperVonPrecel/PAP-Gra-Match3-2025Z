package pap.project.game_history.model;

import jakarta.persistence.PersistenceException;

public class DataNotFoundException extends PersistenceException
{
    public DataNotFoundException(String errorMessage)
    {
        super(errorMessage);
    }
}
