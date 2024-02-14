module FunctionsPoint
{
    interface Observer
    {
        void update(string command ,string msg);
        void shutdownObserver();
    }
    interface Subject
    {
        void addObserver(Observer* o);
        void removeObserver(Observer* o);
        void getTask();
        void addPartialResult(string fileNameResult);
    }
}