package com.victorengineer.limpiamiciudad.util;

import com.victorengineer.limpiamiciudad.models.Result;

public interface ResultListener <I> {

    void onResult(Result result, I instance);

}
