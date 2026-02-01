package com.safecall.recorder;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.safecall.recorder.data.local.db.RecordingEntity;
import com.safecall.recorder.data.repository.RecordingRepository;
import com.safecall.recorder.databinding.ActivityRecordingDetailsBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity for displaying recording details and playback controls.
 */
@AndroidEntryPoint
public class RecordingDetailsActivity extends AppCompatActivity {

    private ActivityRecordingDetailsBinding binding;
    private MediaPlayer mediaPlayer;
    private RecordingEntity recording;
    private File decryptedFile;
    private boolean isPlaying = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Inject
    RecordingRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecordingDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        long recordingId = getIntent().getLongExtra("recording_id", -1);
        if (recordingId == -1) {
            finish();
            return;
        }

        // Load recording in background
        executor.execute(() -> {
            recording = repository.getRecordingById(recordingId);
            if (recording == null) {
                runOnUiThread(this::finish);
                return;
            }
            runOnUiThread(this::setupUI);
        });
    }

    private void setupUI() {
        // Setup toolbar
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_rename) {
                showRenameDialog();
                return true;
            } else if (id == R.id.action_share) {
                shareRecording();
                return true;
            } else if (id == R.id.action_delete) {
                showDeleteConfirmation();
                return true;
            }
            return false;
        });

        // Set recording info
        binding.name.setText(recording.getDisplayName());
        binding.phoneNumber.setText(recording.getPhoneNumber());
        binding.callType.setText(
                recording.isIncoming() ? getString(R.string.incoming_call) : getString(R.string.outgoing_call));
        binding.timestamp.setText(formatFullTimestamp(recording.getTimestamp()));
        binding.duration.setText(formatDuration(recording.getDuration()));
        binding.fileSize.setText(formatFileSize(recording.getFileSize()));
        binding.encrypted.setText(recording.isEncrypted() ? "Yes" : "No");
        binding.backedUp.setText(recording.isBackedUp() ? "Yes" : "No");

        // Favorites
        updateFavoriteUI(recording.isFavorite());
        binding.favoriteButton.setOnClickListener(v -> {
            boolean newState = !recording.isFavorite();
            recording.setFavorite(newState);
            repository.toggleFavorite(recording.getId(), newState);
            updateFavoriteUI(newState);
        });

        // Notes
        binding.notesInput.setText(recording.getNotes());
        binding.notesInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String notes = binding.notesInput.getText().toString();
                repository.updateNotes(recording.getId(), notes);
            }
        });

        // Setup call type icon
        binding.callTypeIcon
                .setImageResource(recording.isIncoming() ? R.drawable.ic_call_received : R.drawable.ic_call_made);

        // Setup playback controls
        binding.playButton.setOnClickListener(v -> togglePlayback());

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    binding.currentPosition.setText(formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        // Initialize player
        initializePlayer();
    }

    private void initializePlayer() {
        executor.execute(() -> {
            try {
                decryptedFile = repository.getDecryptedFileForPlayback(recording);
                runOnUiThread(() -> {
                    try {
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(decryptedFile.getAbsolutePath());
                        mediaPlayer.prepare();

                        binding.seekBar.setMax(mediaPlayer.getDuration());
                        binding.totalDuration.setText(formatDuration(mediaPlayer.getDuration()));

                        mediaPlayer.setOnCompletionListener(mp -> {
                            isPlaying = false;
                            binding.playButton.setImageResource(R.drawable.ic_play);
                            binding.seekBar.setProgress(0);
                            binding.currentPosition.setText("0:00");
                        });

                    } catch (Exception e) {
                        Toast.makeText(this, "Error loading audio", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error decrypting file", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void togglePlayback() {
        if (mediaPlayer == null)
            return;

        if (isPlaying) {
            mediaPlayer.pause();
            binding.playButton.setImageResource(R.drawable.ic_play);
            handler.removeCallbacksAndMessages(null);
        } else {
            mediaPlayer.start();
            binding.playButton.setImageResource(R.drawable.ic_pause);
            updateSeekBar();
        }
        isPlaying = !isPlaying;
    }

    private void updateSeekBar() {
        if (mediaPlayer != null && isPlaying) {
            binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
            binding.currentPosition.setText(formatDuration(mediaPlayer.getCurrentPosition()));
            handler.postDelayed(this::updateSeekBar, 100);
        }
    }

    private void showRenameDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setText(recording.getCustomName() != null ? recording.getCustomName() : recording.getContactName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.rename)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        repository.renameRecording(recording.getId(), newName);
                        recording.setCustomName(newName);
                        binding.name.setText(newName);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void shareRecording() {
        executor.execute(() -> {
            try {
                File shareFile = repository.getFileForSharing(recording);
                runOnUiThread(() -> {
                    android.net.Uri uri = FileProvider.getUriForFile(
                            this,
                            getPackageName() + ".fileprovider",
                            shareFile);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("audio/wav");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "Share Recording"));
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error sharing file", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.delete_confirmation)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    repository.deleteRecording(recording);
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void updateFavoriteUI(boolean isFavorite) {
        binding.favoriteButton.setIconResource(isFavorite ? R.drawable.ic_star : R.drawable.ic_star_border);
    }

    private String formatFullTimestamp(long timestamp) {
        return new SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
                .format(new Date(timestamp));
    }

    private String formatDuration(long durationMs) {
        long seconds = (durationMs / 1000) % 60;
        long minutes = (durationMs / (1000 * 60)) % 60;
        long hours = durationMs / (1000 * 60 * 60);

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
        }
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format(Locale.getDefault(), "%.1f KB", bytes / 1024.0);
        } else {
            return String.format(Locale.getDefault(), "%.1f MB", bytes / (1024.0 * 1024.0));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (decryptedFile != null && decryptedFile.exists()) {
            decryptedFile.delete();
        }
        executor.shutdown();
    }
}
