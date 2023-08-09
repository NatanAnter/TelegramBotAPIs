package org.example.MainDataFlow;

public class Phase {
    private Enum<?> phase;

    public Phase(Enum<?> phase) {
        this.phase = phase;
    }

    public Enum<?> getPhase() {
        return phase;
    }

    public void setPhase(Enum<?> phase) {
        this.phase = phase;
    }

    public enum NewUser {
        firstLine, second

    }

    public enum Home {
        home,


    }

    public enum CatAPI {
        first,

    }

    public enum CountryAPI {
        first, setting, countryCode, yes, no

    }

    public enum JokesAPI {
        first, category, setting

    }

    public enum NewsAPI {
        first, main, settings_main, setting_site_main, setting_site_name, setting_site_source, setting_category1, setting_category1_country, setting_category2, setting_category2_country, setting_times

    }

    public enum NumbersAPI {
        first, specific_number

    }


}
