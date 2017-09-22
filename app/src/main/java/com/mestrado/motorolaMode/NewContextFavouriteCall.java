package com.mestrado.motorolaMode;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.mestrado.ufahamu.ContextState;
import com.mestrado.ufahamu.NewContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by pma035 on 2/24/17.
 */

public class NewContextFavouriteCall extends NewContext {
    private List<NumberCall> listPhoneNumbers = new ArrayList<>();
    private Context parentContext;

    @Override
    protected Date doDefineFirstTimeContextCheck() {
        return null;
    }

    @Override
    protected int doDefineContextualCheckTimes() {
        return 0;
    }

    @Override
    protected long doDefineContextCheckFrequency() {
        return 3600000;
    }

    @Override
    protected void doCreateNewContext() {
        parentContext = getRules().iterator().next().getParentActivity();
        TelephonyManager myTelephonyManager = (TelephonyManager) parentContext
                .getSystemService(android.content.Context.TELEPHONY_SERVICE);

        Looper.prepare();
        myTelephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    Log.i(MyActivity.LOG, "Phone ringing");
                    if (incomingNumber != null && !incomingNumber.isEmpty()) {
                        Log.i(MyActivity.LOG, "Incoming number: " + incomingNumber);
                        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup
                                .CONTENT_FILTER_URI, Uri.encode(incomingNumber));
                        String[] numberProjection = {ContactsContract.PhoneLookup._ID,
                                ContactsContract.PhoneLookup.NUMBER,
                                ContactsContract.PhoneLookup.DISPLAY_NAME,
                                ContactsContract.PhoneLookup.STARRED};
                        Cursor cursor = parentContext.getContentResolver()
                                .query(uri, numberProjection, null, null, null);

                        if (cursor != null && cursor.moveToFirst()
                                && cursor.getInt(cursor.getColumnIndex(
                                ContactsContract.PhoneLookup.STARRED)) == 1) {
                            //Número recebido é favorito
                            Log.i(MyActivity.LOG, "Favourite number");
                            setState(ContextState.CHECKED);
                        } else {
                            boolean numberCalled = true;

                            for (NumberCall n : listPhoneNumbers) {
                                if (n.getPhoneNumber().equals(incomingNumber)) {
                                    Date actualDate = new Date();
                                    Calendar lastCallDateAfter5Min = Calendar.getInstance();
                                    lastCallDateAfter5Min.setTime(n.getLastCallDate());
                                    lastCallDateAfter5Min.set(Calendar.MINUTE, lastCallDateAfter5Min.get(Calendar.MINUTE) + 5);

                                    if (actualDate.before(lastCallDateAfter5Min.getTime())) {
                                        //Mesmo número ligou dentro de 5min
                                        Log.i(MyActivity.LOG, "Repeated number IN 5min");
                                        setState(ContextState.CHECKED);
                                    } else {
                                        Log.i(MyActivity.LOG, "Repeated number OUT 5min");
                                        setState(ContextState.UNCHECKED);
                                    }

                                    n.setLastCallDate(actualDate);
                                    numberCalled = false;
                                    break;
                                }
                            }

                            if (numberCalled) {
                                NumberCall numberCall = new NumberCall();
                                numberCall.setPhoneNumber(incomingNumber);
                                numberCall.setLastCallDate(new Date());
                                listPhoneNumbers.add(numberCall);
                                setState(ContextState.UNCHECKED);
                            }
                        }
                    } else {
                        setState(ContextState.UNCHECKED);
                    }
                } else {
                    setState(ContextState.UNCHECKED);
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
        Looper.loop();
    }

    private class NumberCall {
        String phoneNumber;
        Date lastCallDate;

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public Date getLastCallDate() {
            return lastCallDate;
        }

        public void setLastCallDate(Date lastCallDate) {
            this.lastCallDate = lastCallDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null) {
                return false;
            }

            if (getClass() != o.getClass()) {
                return false;
            }

            NumberCall numberCall = (NumberCall) o;

            return (phoneNumber == numberCall.getPhoneNumber()
                    || phoneNumber != null && phoneNumber.equals(numberCall.getPhoneNumber()))
                    && (lastCallDate == numberCall.getLastCallDate()
                    || lastCallDate != null && lastCallDate.equals(numberCall.getLastCallDate()));
        }

        @Override
        public int hashCode() {
            int primeNumber = 37;
            int result = 1;

            result = primeNumber * result + (phoneNumber == null ? 0 : phoneNumber.hashCode());
            result = primeNumber * result + (lastCallDate == null ? 0 : lastCallDate.hashCode());

            return result;
        }
    }
}
