package course.examples.Services.KeyCommon;

    interface KeyGenerator {
        String getArtist(int id);
        String getName(int id);
        String getURL(int id);
        String[] getAll(int id);
    }