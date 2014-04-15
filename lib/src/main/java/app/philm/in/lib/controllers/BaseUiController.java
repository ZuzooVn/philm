package app.philm.in.lib.controllers;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.inject.Inject;

import app.philm.in.lib.Constants;
import app.philm.in.lib.Display;
import app.philm.in.lib.util.Logger;
import app.philm.in.lib.util.TextUtils;

abstract class BaseUiController<U extends BaseUiController.Ui<UC>, UC>
        extends BaseController {

    public interface Ui<UC> {

        String getUiTitle();

        void setCallbacks(UC callbacks);

        boolean isModal();
    }

    public interface SubUi {
    }

    @Inject Logger mLogger;

    private final Set<U> mUis;
    private final Set<U> mUnmodifiableUis;

    public BaseUiController() {
        mUis = new CopyOnWriteArraySet<>();
        mUnmodifiableUis = Collections.unmodifiableSet(mUis);
    }

    public synchronized final void attachUi(U ui) {
        Preconditions.checkArgument(ui != null, "ui cannot be null");
        Preconditions.checkState(!mUis.contains(ui), "UI is already attached");

        mUis.add(ui);

        ui.setCallbacks(createUiCallbacks(ui));

        if (isInited()) {
            if (!ui.isModal() && !(ui instanceof SubUi)) {
                final String uiTitle = ui.getUiTitle();
                if (!TextUtils.isEmpty(uiTitle)) {
                    updateDisplayTitle(ui.getUiTitle());
                }
            }

            onUiAttached(ui);
            populateUis();
        }
    }

    protected void updateDisplayTitle(String title) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title);
        }
    }

    protected void updateDisplayTitle(String title, int color) {
        Display display = getDisplay();
        if (display != null) {
            display.setActionBarTitle(title, color);
        }
    }

    public synchronized final void detachUi(U ui) {
        Preconditions.checkArgument(ui != null, "ui cannot be null");
        Preconditions.checkState(mUis.contains(ui), "ui is not attached");
        onUiDetached(ui);
        ui.setCallbacks(null);

        mUis.remove(ui);
    }

    protected final Set<U> getUis() {
        return mUnmodifiableUis;
    }

    protected void onInited() {
        if (!mUis.isEmpty()) {
            for (U ui : mUis) {
                onUiAttached(ui);
            }
            populateUis();
        }
    }

    protected void onUiAttached(U ui) {
    }

    protected void onUiDetached(U ui) {
    }

    protected synchronized void populateUis() {
        if (Constants.DEBUG) {
            mLogger.d(getClass().getSimpleName(), "populateUis");
        }
        for (U ui : mUis) {
            populateUi(ui);
        }
    }

    protected void populateUi(U ui) {
    }

    protected abstract UC createUiCallbacks(U ui);

    protected int getId(U ui) {
        return ui.hashCode();
    }

    protected synchronized U findUi(final int id) {
        for (U ui : mUis) {
            if (getId(ui) == id) {
                return ui;
            }
        }
        return null;
    }

}