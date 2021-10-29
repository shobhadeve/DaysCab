package com.dayscab.utils;


import com.dayscab.common.models.ModelCurrentBooking;

public interface onSearchingDialogListener {
    void onRequestAccepted(ModelCurrentBooking data);
    void onRequestCancel();
    void onDriverNotFound();
}
