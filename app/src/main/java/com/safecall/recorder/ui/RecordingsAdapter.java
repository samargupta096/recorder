package com.safecall.recorder.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.safecall.recorder.R;
import com.safecall.recorder.data.local.db.RecordingEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * RecyclerView adapter for displaying recordings.
 */
public class RecordingsAdapter extends ListAdapter<RecordingEntity, RecordingsAdapter.RecordingViewHolder> {

    private final Context context;
    private final OnRecordingClickListener clickListener;
    private final OnRecordingDeleteListener deleteListener;

    public interface OnRecordingClickListener {
        void onClick(RecordingEntity recording);
    }

    public interface OnRecordingDeleteListener {
        void onDelete(RecordingEntity recording);
    }

    public RecordingsAdapter(Context context, OnRecordingClickListener clickListener,
            OnRecordingDeleteListener deleteListener) {
        super(new RecordingDiffCallback());
        this.context = context;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public RecordingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recording, parent, false);
        return new RecordingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordingViewHolder holder, int position) {
        RecordingEntity recording = getItem(position);
        holder.bind(recording);
    }

    class RecordingViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconView;
        private final TextView nameView;
        private final TextView detailsView;
        private final TextView durationView;
        private final ImageView encryptedIcon;
        private final ImageView backedUpIcon;
        private final ImageView favoriteIcon;
        private final ImageView deleteButton;

        RecordingViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.icon);
            nameView = itemView.findViewById(R.id.name);
            detailsView = itemView.findViewById(R.id.details);
            durationView = itemView.findViewById(R.id.duration);
            encryptedIcon = itemView.findViewById(R.id.encrypted_icon);
            backedUpIcon = itemView.findViewById(R.id.backed_up_icon);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        void bind(RecordingEntity recording) {
            // Set call direction icon
            iconView.setImageResource(recording.isIncoming() ? R.drawable.ic_call_received : R.drawable.ic_call_made);

            // Set name
            nameView.setText(recording.getDisplayName());

            // Set details (date/time)
            String timestamp = formatTimestamp(recording.getTimestamp());
            detailsView.setText(timestamp);

            // Set duration
            durationView.setText(formatDuration(recording.getDuration()));

            // Set status icons
            encryptedIcon.setVisibility(recording.isEncrypted() ? View.VISIBLE : View.GONE);
            backedUpIcon.setVisibility(recording.isBackedUp() ? View.VISIBLE : View.GONE);
            favoriteIcon.setVisibility(recording.isFavorite() ? View.VISIBLE : View.GONE);

            // Click listener
            itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onClick(recording);
                }
            });

            // Delete button
            deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(recording);
                }
            });
        }

        private String formatTimestamp(long timestamp) {
            long now = System.currentTimeMillis();
            long diff = now - timestamp;

            if (diff < 24 * 60 * 60 * 1000) {
                return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date(timestamp));
            } else if (diff < 48 * 60 * 60 * 1000) {
                return "Yesterday";
            } else if (diff < 7 * 24 * 60 * 60 * 1000) {
                return new SimpleDateFormat("EEEE", Locale.getDefault()).format(new Date(timestamp));
            } else {
                return new SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(new Date(timestamp));
            }
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
    }

    private static class RecordingDiffCallback extends DiffUtil.ItemCallback<RecordingEntity> {
        @Override
        public boolean areItemsTheSame(@NonNull RecordingEntity oldItem, @NonNull RecordingEntity newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull RecordingEntity oldItem, @NonNull RecordingEntity newItem) {
            return oldItem.getTimestamp() == newItem.getTimestamp() &&
                    oldItem.isBackedUp() == newItem.isBackedUp() &&
                    (oldItem.getCustomName() == null ? newItem.getCustomName() == null
                            : oldItem.getCustomName().equals(newItem.getCustomName()));
        }
    }
}
