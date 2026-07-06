package com.dubai.dubai.dto;

import java.util.List;

public class ReemplazarCaracteristicasRequest {

    private List<Long> caracteristicaIds;

    public ReemplazarCaracteristicasRequest() {
    }

    public ReemplazarCaracteristicasRequest(List<Long> caracteristicaIds) {
        this.caracteristicaIds = caracteristicaIds;
    }

    public List<Long> getCaracteristicaIds() {
        return caracteristicaIds;
    }

    public void setCaracteristicaIds(List<Long> caracteristicaIds) {
        this.caracteristicaIds = caracteristicaIds;
    }
}