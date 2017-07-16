package online.findfootball.android.background.tasks;

import android.content.Context;
import android.os.AsyncTask;

import java.util.HashSet;

/**
 * Created by WiskiW on 04.06.2017.
 */

public class BgTaskMaker extends AsyncTask<Void, Void, Integer> {

    private HashSet<BgTask> taskList = new HashSet<>();
    private OnBgCompleteListener listener;
    private Context context;

    private BgTaskMaker(Context context) {
        this.context = context;
    }

    public static BgTaskMaker newMaker(Context context) {
        return new BgTaskMaker(context);
    }

    public OnBgCompleteListener getCompleteListener() {
        return listener;
    }

    public BgTaskMaker setCompleteListener(OnBgCompleteListener listener) {
        this.listener = listener;
        return this;
    }

    public BgTaskMaker setContext(Context context) {
        this.context = context;
        return this;
    }

    public BgTaskMaker add(BgTask task) {
        taskList.add(task);
        return this;
    }

    public void makeAll() {
        if (getStatus() != AsyncTask.Status.RUNNING) {
            execute();
        }
    }

    public void make(BgTask task) {
        add(task);
        makeAll();
    }

    public void stopAll() {
        cancel(false);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        for (BgTask aTaskList : taskList) {
            aTaskList.doInBackground(context);
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        super.onPostExecute(resultCode);
        if (listener != null) {
            listener.onComplete();
        }

    }

    public interface OnBgCompleteListener {
        // TODO : put onFailed() & onSuccess()
        void onComplete();
    }

}
