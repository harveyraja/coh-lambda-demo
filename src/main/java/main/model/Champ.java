package main.model;

import java.io.Serializable;

/**
 * Champ holds the name and year for the winner of the F1 championship.
 *
 * @author hr  2017.04.19
 */
public class Champ
        implements Serializable
    {
    // ----- constructors ---------------------------------------------------

    public Champ()
        {}

    public Champ(int nYear, String sName)
        {
        m_nYear = nYear;
        m_sName = sName;
        }

    // ----- accessors ------------------------------------------------------

    public String getName()
        {
        return m_sName;
        }

    public void setName(String sName)
        {
        m_sName = sName;
        }

    public int getYear()
        {
        return m_nYear;
        }

    public void setYear(int nYear)
        {
        m_nYear = nYear;
        }

    @Override
    public String toString()
        {
        return "Champ{" +
                "year=" + m_nYear +
                ", name='" + m_sName + '\'' +
                '}';
        }

    // ----- data members ---------------------------------------------------

    protected int    m_nYear;
    protected String m_sName;
    }
