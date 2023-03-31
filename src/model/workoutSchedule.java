package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class workoutSchedule {
    // Declare a static List that will store the workout reminders
    private static List<workoutSchedule> workoutReminders;

    // Declare instance variables for workout and reminder time
    private Workout workout;
    private LocalDateTime reminderTime;

    // Constructor that initializes the instance variables for workout and reminder time
    public workoutSchedule(Workout workout, LocalDateTime reminderTime) {
        this.workout = workout;
        this.reminderTime = reminderTime;
    }

    // Method to add a workout reminder to the workoutReminders List
    public static void addWorkoutReminder(Workout workout, int reminderTimeMinutes) {
        // Calculate the reminder time based on the current time and the given reminder time in minutes
        LocalDateTime reminderTime = LocalDateTime.now().plus(Duration.ofMinutes(reminderTimeMinutes));

        // Create a new WorkoutReminder object with the given workout and reminder time
        workoutSchedule reminder = new workoutSchedule(workout, reminderTime);

        // Initialize the workoutReminders List if it hasn't been created yet
        if (workoutReminders == null) {
            workoutReminders = new ArrayList<>();
        }

        // Add the WorkoutReminder to the workoutReminders List
        workoutReminders.add(reminder);

        // Print a confirmation message to the console
        System.out.println("Reminder set for workout \"" + workout.getName() + "\" at " + reminderTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    // Method to remove a workout reminder from the workoutReminders List
    public static void removeWorkoutReminder(Workout workout) {
        // Loop through the workoutReminders List and remove the first WorkoutReminder with the given workout
        if (workoutReminders != null) {
            for (int i = 0; i < workoutReminders.size(); i++) {
                workoutSchedule reminder = workoutReminders.get(i);
                if (reminder.workout.equals(workout)) {
                    workoutReminders.remove(i);
                    System.out.println("Reminder removed for workout \"" + workout.getName() + "\"");
                    return;
                }
            }
        }
        System.out.println("No reminder found for workout \"" + workout.getName() + "\"");
    }

    // Getter methods for the instance variables
    public Workout getWorkout() {
        return workout;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    // Method to display all the workout reminders in the workoutReminders List
    public static void showWorkoutReminders() {
        try {
            System.out.println("Workout Reminders:");
            if (workoutReminders != null) {
                for (workoutSchedule reminder : workoutReminders) {
                    System.out.print("- " + reminder.getWorkout().getName());
                    if (reminder.getReminderTime() != null) {
                        System.out.print(" (reminder set for " + reminder.getReminderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + ")");
                    }
                    System.out.println();
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}