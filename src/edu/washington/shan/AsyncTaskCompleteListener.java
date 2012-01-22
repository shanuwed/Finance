package edu.washington.shan;

/** 
 * 
 * For more info see:
 * http://stackoverflow.com/questions/3291490/common-class-for-asynctask-in-android
 * @param <T>
 */
public interface AsyncTaskCompleteListener<T> {
    public void onTaskComplete(T result);
}
