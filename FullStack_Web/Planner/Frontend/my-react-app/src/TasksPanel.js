import React, { useState, useEffect } from 'react';

const TasksPanel = () => {
  const [tasks, setTasks] = useState([]);
  const [currentPriorityFilter, setCurrentPriorityFilter] = useState('high');
  const [newTaskText, setNewTaskText] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const TASKS_STORAGE_KEY = 'planner_tasks';

  const getTasksFromLocalStorage = () => {
    const storedTasks = localStorage.getItem(TASKS_STORAGE_KEY);
    return storedTasks ? JSON.parse(storedTasks) : [];
  };

  const saveTasksToLocalStorage = (tasksToSave) => {
    localStorage.setItem(TASKS_STORAGE_KEY, JSON.stringify(tasksToSave));
  };

  const fetchTasks = () => {
    setLoading(true);
    setError(null);
    // TODO: Future API Integration - Fetch tasks from backend API
    // const token = localStorage.getItem('sessionToken');
    // if (!token) { /* Handle unauthenticated state */ }
    // try { /* API call to GET /api/tasks */ } catch (err) { /* Handle API error */ }
    
    const localTasks = getTasksFromLocalStorage();
    setTasks(localTasks);
    setLoading(false);
  };

  useEffect(() => {
    fetchTasks();
  }, []);

  const handleAddTask = (e) => {
    e.preventDefault();
    if (newTaskText.trim()) {
      const newTask = {
        id: Date.now(), // Simple unique ID for local storage
        text: newTaskText,
        priority: currentPriorityFilter,
      };

      // TODO: Future API Integration - Add task to backend API
      // const token = localStorage.getItem('sessionToken');
      // if (!token) { /* Handle unauthenticated state */ }
      // try { /* API call to POST /api/tasks with newTask */ } catch (err) { /* Handle API error */ }

      const updatedTasks = [...getTasksFromLocalStorage(), newTask];
      saveTasksToLocalStorage(updatedTasks);
      setTasks(updatedTasks);
      setNewTaskText('');
    }
  };

  const handleDeleteTask = (id) => {
    // TODO: Future API Integration - Delete task from backend API
    // const token = localStorage.getItem('sessionToken');
    // if (!token) { /* Handle unauthenticated state */ }
    // try { /* API call to DELETE /api/tasks/${id} */ } catch (err) { /* Handle API error */ }

    const updatedTasks = getTasksFromLocalStorage().filter(task => task.id !== id);
    saveTasksToLocalStorage(updatedTasks);
    setTasks(updatedTasks);
  };

  const handleDragStart = (e, task) => {
    e.dataTransfer.setData('application/json', JSON.stringify(task));
  };

  const filteredTasks = tasks.filter(task => task.priority === currentPriorityFilter);

  if (loading) {
    return <p style={{ textAlign: 'center' }}>Loading tasks...</p>;
  }

  return (
    <section className="panel tasks-panel">
      <div className="panel-header">
        <h2>Tasks</h2>
        <div className="window-controls">
          <button className="window-control-btn minimize"></button>
          <button className="window-control-btn maximize"></button>
          <button className="window-control-btn close"></button>
        </div>
      </div>
      <div className="panel-content">
        {error && <p style={{ textAlign: 'center', color: 'red', marginBottom: '10px' }}>{error}</p>}
        <div className="priority-tabs">
          <button
            className={`priority-button ${currentPriorityFilter === 'high' ? 'active' : ''}`}
            data-priority="high"
            onClick={() => setCurrentPriorityFilter('high')}
          >
            High
          </button>
          <button
            className={`priority-button ${currentPriorityFilter === 'medium' ? 'active' : ''}`}
            data-priority="medium"
            onClick={() => setCurrentPriorityFilter('medium')}
          >
            Medium
          </button>
          <button
            className={`priority-button ${currentPriorityFilter === 'low' ? 'active' : ''}`}
            data-priority="low"
            onClick={() => setCurrentPriorityFilter('low')}
          >
            Low
          </button>
        </div>
        <div className="task-list">
          {filteredTasks.length === 0 ? (
            <p style={{ textAlign: 'center', color: '#777', fontSize: '0.9rem' }}>No tasks for this priority.</p>
          ) : (
            filteredTasks.map(task => (
              <div 
                key={task.id} 
                className={`task-item ${task.priority}`}
                draggable="true"
                onDragStart={(e) => handleDragStart(e, task)}
              >
                <span>{task.text}</span>
                <button className="delete-btn" onClick={() => handleDeleteTask(task.id)}>❌</button>
              </div>
            ))
          )}
        </div>
        <div className="add-task">
          <input
            type="text"
            id="newTaskInput"
            placeholder={`Add new ${currentPriorityFilter}-priority task`}
            value={newTaskText}
            onChange={(e) => setNewTaskText(e.target.value)}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                handleAddTask(e);
              }
            }}
          />
          <button id="addTaskButton" onClick={handleAddTask}>➕</button>
        </div>
      </div>
    </section>
  );
};

export default TasksPanel;