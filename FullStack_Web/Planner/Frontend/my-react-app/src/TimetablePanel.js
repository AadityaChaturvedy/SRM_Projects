import React, { useState, useEffect } from 'react';

const TimetablePanel = () => {
  const [subjectBlocks, setSubjectBlocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const days = ['DO-1', 'DO-2', 'DO-3', 'DO-4', 'DO-5'];
  const timeSlots = [
    '8:00 - 8:50',
    '8:50 - 9:40',
    '9:45 - 10:35',
    '10:40 - 11:30',
    '11:35 - 12:25',
    '12:30 - 1:20',
    '1:25 - 2:15',
    '2:20 - 3:10',
    '3:10 - 4:00',
    '4:00 - 4:50'
  ];
  const courseColorsPalette = [
    { bg: '#e0f7fa', text: '#00796b' },
    { bg: '#ffe0b2', text: '#e65100' },
    { bg: '#c8e6c9', text: '#2e7d32' },
    { bg: '#ffcdd2', text: '#c62828' },
    { bg: '#bbdefb', text: '#1565c0' },
    { bg: '#f8bbd0', text: '#ad1457' },
    { bg: '#d1c4e9', text: '#4527a0' },
    { bg: '#fff9c4', text: '#fbc02d' },
    { bg: '#b2ebf2', text: '#00838f' },
    { bg: '#f0f4c3', text: '#558b2f' },
  ];

  const courseColors = new Map();
  let colorIndex = 0;

  const getCourseColor = (courseCode) => {
    if (!courseColors.has(courseCode)) {
      courseColors.set(courseCode, courseColorsPalette[colorIndex]);
      colorIndex = (colorIndex + 1) % courseColorsPalette.length;
    }
    return courseColors.get(courseCode);
  };

  const getTaskGradient = (priority) => {
    switch (priority) {
      case 'high':
        return 'linear-gradient(to bottom, #ffefef, #ffcdd2)';
      case 'medium':
        return 'linear-gradient(to bottom, #fff8e1, #ffecb3)';
      case 'low':
        return 'linear-gradient(to bottom, #e8f5e9, #c8e6c9)';
      default:
        return 'linear-gradient(to bottom, #f5f5f5, #e0e0e0)';
    }
  };

  const formatTime = (timeString) => {
    const [hour, minute] = timeString.split(':');
    return `${parseInt(hour)}:${minute}`;
  };

  const getSlotStartTime = (slotString) => {
    const [startTimePart] = slotString.split(' ');
    return formatTime(startTimePart);
  };

  const [showPopup, setShowPopup] = useState(false);
  const [popupContent, setPopupContent] = useState(null);

  const handleCardClick = (block) => {
    setPopupContent(block);
    setShowPopup(true);
  };

  const handleClosePopup = () => {
    setShowPopup(false);
    setPopupContent(null);
  };

  const handleRemoveBlock = (blockToRemove) => {
    setSubjectBlocks(subjectBlocks.filter(block => block.id !== blockToRemove.id));
    handleClosePopup();
  };

  const handleRemoveAllTasks = (taskId) => {
    setSubjectBlocks(subjectBlocks.filter(block => block.taskId !== taskId));
    handleClosePopup();
  };

  const handleDragOver = (e) => {
    e.preventDefault();
  };

  const handleDrop = (e, day, time) => {
    e.preventDefault();
    const task = JSON.parse(e.dataTransfer.getData('application/json'));
    const newBlock = {
      id: `${task.id}-${day}-${time}`,
      taskId: task.id,
      day: day,
      time: time,
      subject: task.text,
      type: 'task',
      priority: task.priority,
      background: getTaskGradient(task.priority),
      textColor: '#333'
    };

    setSubjectBlocks([...subjectBlocks.filter(b => b.day !== day || b.time !== time), newBlock]);
  };

  const getBlockStyle = (block) => {
    if (!block) {
      return { border: '1px dashed #b2ebf2' };
    }

    const style = {
      color: block.textColor,
      borderRadius: '8px',
      fontWeight: '600',
      border: 'none'
    };

    if (block.background) {
      style.background = block.background;
    } else {
      style.backgroundColor = block.bgColor;
    }

    return style;
  };

  useEffect(() => {
    const fetchTimetableData = async () => {
      const token = localStorage.getItem('sessionToken');
      if (!token) {
        console.warn('No session token found. Timetable data will not be fetched.');
        setLoading(false);
        return;
      }

      try {
        const response = await fetch('http://localhost:8080/api/academia/timetable', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (!response.ok) {
          if (response.status === 401) {
            console.error('Authentication failed. Redirecting to login.');
            localStorage.removeItem('sessionToken');
            window.location.href = '/';
          }
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        console.log('Timetable data fetched:', data);

        const processedSubjectBlocks = [];
        const timetableArray = data.data && data.data.timetable ? data.data.timetable : data.timetable;

        if (timetableArray) {
          timetableArray.forEach(dayEntry => {
          const dayName = days[dayEntry.dayOrder - 1];
          dayEntry.schedule.forEach(entry => {
            if (entry.title) {
              const formattedStartTime = formatTime(entry.startTime);
              const matchingTimeSlot = timeSlots.find(slot => getSlotStartTime(slot) === formattedStartTime);

            if (matchingTimeSlot) {
              const { bg, text } = getCourseColor(entry.courseCode);
              processedSubjectBlocks.push({
                id: `${dayName}-${matchingTimeSlot}`,
                day: dayName,
                time: matchingTimeSlot,
                subject: entry.title,
                courseCode: entry.courseCode,
                faculty: entry.faculty,
                location: entry.room,
                bgColor: bg,
                textColor: text,
                type: 'class'
              });
            } else {
              console.warn(`No matching time slot found for entry: ${entry.title} at ${formattedStartTime}`);
            }
          }
          });
        });
        setSubjectBlocks(processedSubjectBlocks);
          }
        } catch (err) {
        console.error('Error fetching timetable data:', err);
        setError('Failed to load timetable. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    fetchTimetableData();
  }, []);

  if (loading) {
    return <p style={{ textAlign: 'center' }}>Loading timetable...</p>;
  }

  if (error) {
    return <p style={{ textAlign: 'center', color: 'red' }}>{error}</p>;
  }

  return (
    <section className="panel timetable-panel" style={{ position: 'relative' }}>
      <div className="panel-header">
        <h2>Timetable</h2>
        <div className="window-controls">
          <button className="window-control-btn minimize"></button>
          <button className="window-control-btn maximize"></button>
          <button className="window-control-btn close"></button>
        </div>
      </div>
      <div className="panel-content">
        <div className="timetable-grid">
          <div></div>

          {days.map(day => (
            <div key={day} className="day-header">{day}</div>
          ))}

          {timeSlots.map(time => (
            <React.Fragment key={time}>
              <div className="time-label">{time}</div>
              {days.map(day => {
                const block = subjectBlocks.find(b => b.day === day && b.time === time);
                return (
                  <div
                    key={`${day}-${time}`}
                    className="timetable-cell"
                    style={getBlockStyle(block)}
                    onClick={() => block && handleCardClick(block)}
                    onDragOver={handleDragOver}
                    onDrop={(e) => handleDrop(e, day, time)}
                  >
                    {block ? block.subject : ''}
                  </div>
                );
              })}
            </React.Fragment>
          ))}
        </div>
      </div>

      {showPopup && popupContent && (
        <div style={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          zIndex: 1000,
          width: '100%',
          height: '100%',
        }} onClick={handleClosePopup}>
          <div style={{
            backgroundColor: '#fff',
            padding: '20px',
            borderRadius: '10px',
            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)',
            maxWidth: '400px',
            width: '90%',
            position: 'relative',
            fontFamily: 'Arial, sans-serif',
            color: '#333',
          }} onClick={e => e.stopPropagation()}>
            <button onClick={handleClosePopup} style={{
              position: 'absolute',
              top: '10px',
              right: '10px',
              background: 'none',
              border: 'none',
              fontSize: '1.2rem',
              cursor: 'pointer',
              color: '#666',
            }}>✖</button>
            <h3 style={{ color: popupContent.textColor, marginBottom: '10px' }}>{popupContent.subject}</h3>
            {popupContent.type === 'class' ? (
              <>
                <p><strong>Course Code:</strong> {popupContent.courseCode}</p>
                <p><strong>Faculty:</strong> {popupContent.faculty}</p>
                <p><strong>Location:</strong> {popupContent.location}</p>
                <p><strong>Time:</strong> {popupContent.time}</p>
                <p><strong>Day:</strong> {popupContent.day}</p>
                <button onClick={() => handleRemoveBlock(popupContent)} style={{
                  marginTop: '10px',
                  padding: '10px 20px',
                  borderRadius: '5px',
                  border: 'none',
                  backgroundColor: '#f44336',
                  color: 'white',
                  cursor: 'pointer'
                }}>Cancel Class</button>
              </>
            ) : (
              <>
                <p><strong>Task:</strong> {popupContent.subject}</p>
                <p><strong>Priority:</strong> {popupContent.priority}</p>
                <p><strong>Time:</strong> {popupContent.time}</p>
                <p><strong>Day:</strong> {popupContent.day}</p>
                <button onClick={() => handleRemoveBlock(popupContent)} style={{
                  marginTop: '10px',
                  padding: '10px 20px',
                  borderRadius: '5px',
                  border: 'none',
                  backgroundColor: '#f44336',
                  color: 'white',
                  cursor: 'pointer'
                }}>Remove</button>
                <button onClick={() => handleRemoveAllTasks(popupContent.taskId)} style={{
                  marginTop: '10px',
                  marginLeft: '10px',
                  padding: '10px 20px',
                  borderRadius: '5px',
                  border: 'none',
                  backgroundColor: '#f44336',
                  color: 'white',
                  cursor: 'pointer'
                }}>Remove All Instances</button>
              </>
            )}
          </div>
        </div>
      )}
    </section>
  );
};

export default TimetablePanel;