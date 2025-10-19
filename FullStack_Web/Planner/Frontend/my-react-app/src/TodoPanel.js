import React, { useState, useEffect } from 'react';

const TodoPanel = () => {
  const [todos, setTodos] = useState([]);
  const [newTodoText, setNewTodoText] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const TODOS_STORAGE_KEY = 'planner_todos';

  const getTodosFromLocalStorage = () => {
    const storedTodos = localStorage.getItem(TODOS_STORAGE_KEY);
    return storedTodos ? JSON.parse(storedTodos) : [];
  };

  const saveTodosToLocalStorage = (todosToSave) => {
    localStorage.setItem(TODOS_STORAGE_KEY, JSON.stringify(todosToSave));
  };

  const fetchTodos = () => {
    setLoading(true);
    setError(null);
    // TODO: Future API Integration - Fetch todos from backend API
    // const token = localStorage.getItem('sessionToken');
    // if (!token) { /* Handle unauthenticated state */ }
    // try { /* API call to GET /api/todos */ } catch (err) { /* Handle API error */ }

    const localTodos = getTodosFromLocalStorage();
    setTodos(localTodos);
    setLoading(false);
  };

  useEffect(() => {
    fetchTodos();
  }, []);

  const handleAddTodo = (e) => {
    e.preventDefault();
    if (newTodoText.trim()) {
      const newTodo = {
        id: Date.now(), // Simple unique ID for local storage
        text: newTodoText,
        completed: false,
      };

      // TODO: Future API Integration - Add todo to backend API
      // const token = localStorage.getItem('sessionToken');
      // if (!token) { /* Handle unauthenticated state */ }
      // try { /* API call to POST /api/todos with newTodo */ } catch (err) { /* Handle API error */ }

      const updatedTodos = [...getTodosFromLocalStorage(), newTodo];
      saveTodosToLocalStorage(updatedTodos);
      setTodos(updatedTodos);
      setNewTodoText('');
    }
  };

  const handleToggleComplete = (id) => {
    // TODO: Future API Integration - Toggle todo completion in backend API
    // const token = localStorage.getItem('sessionToken');
    // if (!token) { /* Handle unauthenticated state */ }
    // try { /* API call to PUT /api/todos/${id} with updatedTodo */ } catch (err) { /* Handle API error */ }

    const updatedTodos = getTodosFromLocalStorage().map(todo =>
      todo.id === id ? { ...todo, completed: !todo.completed } : todo
    );
    saveTodosToLocalStorage(updatedTodos);
    setTodos(updatedTodos);
  };

  const handleDeleteTodo = (id) => {
    // TODO: Future API Integration - Delete todo from backend API
    // const token = localStorage.getItem('sessionToken');
    // if (!token) { /* Handle unauthenticated state */ }
    // try { /* API call to DELETE /api/todos/${id} */ } catch (err) { /* Handle API error */ }

    const updatedTodos = getTodosFromLocalStorage().filter(todo => todo.id !== id);
    saveTodosToLocalStorage(updatedTodos);
    setTodos(updatedTodos);
  };

  const uncompletedTodos = todos.filter(todo => !todo.completed);
  const completedTodos = todos.filter(todo => todo.completed);

  if (loading) {
    return <p style={{ textAlign: 'center' }}>Loading to-do items...</p>;
  }

  return (
    <section className="panel todo-panel">
      <div className="panel-header">
        <h2>To-Do List</h2>
        <div className="window-controls">
          <button className="window-control-btn minimize"></button>
          <button className="window-control-btn maximize"></button>
          <button className="window-control-btn close"></button>
        </div>
      </div>
      <div className="panel-content">
        {error && <p style={{ textAlign: 'center', color: 'red', marginBottom: '10px' }}>{error}</p>}
        <div className="todo-list">
          {uncompletedTodos.length === 0 && completedTodos.length === 0 ? (
            <p style={{ textAlign: 'center', color: '#777', fontSize: '0.9rem' }}>No to-do items.</p>
          ) : (
            uncompletedTodos.map(todo => (
              <div key={todo.id} className="todo-item">
                <label className="checkbox-container">
                  <input
                    type="checkbox"
                    checked={todo.completed}
                    onChange={() => handleToggleComplete(todo.id)}
                  />
                  <span className="checkmark"></span>
                </label>
                <span>{todo.text}</span>
                <button className="delete-btn" onClick={() => handleDeleteTodo(todo.id)}>❌</button>
              </div>
            ))
          )}
        </div>
        <div className="completed-section-header">Completed ({completedTodos.length})</div>
        <div className="completed-todo-list">
          {completedTodos.map(todo => (
            <div key={todo.id} className="completed-todo-item">
              <span className="check-icon">✔️</span>
              <span>{todo.text}</span>
              <button className="delete-btn" onClick={() => handleDeleteTodo(todo.id)}>❌</button>
            </div>
          ))}
        </div>
        <div className="add-todo">
          <input
            type="text"
            id="newTodoInput"
            placeholder="Add new to-do item"
            value={newTodoText}
            onChange={(e) => setNewTodoText(e.target.value)}
            onKeyPress={(e) => {
              if (e.key === 'Enter') {
                handleAddTodo(e);
              }
            }}
          />
          <button id="addTodoButton" onClick={handleAddTodo}>➕</button>
        </div>
      </div>
    </section>
  );
};

export default TodoPanel;