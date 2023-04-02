package my.service.responseentity;

import my.service.dto.FeedDTO;

public class FeedResponseEntity extends BasicResponseEntity {

    private FeedDTO data;

    public FeedDTO getData() {
        return data;
    }

    public void setData(FeedDTO data) {
        this.data = data;
    }
}
