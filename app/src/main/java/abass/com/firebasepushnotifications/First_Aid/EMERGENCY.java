package abass.com.firebasepushnotifications.First_Aid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import abass.com.firebasepushnotifications.R;

public class EMERGENCY extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);

        ImageButton btnBURN = view.findViewById(R.id.ImageButton01);
        btnBURN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Openburn();
            }
        });

        ImageButton btnamputation = view.findViewById(R.id.ImageButton02);
        btnamputation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openamputation();
            }
        });

        ImageButton btnasthma = view.findViewById(R.id.ImageButton03);
        btnasthma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openasthma();
            }
        });

        ImageButton btnbleeding = view.findViewById(R.id.ImageButton04);
        btnbleeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openbleeding();
            }
        });

        ImageButton btnchoking = view.findViewById(R.id.ImageButton05);
        btnchoking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openchoking();
            }
        });

        ImageButton btndogbite = view.findViewById(R.id.ImageButton06);
        btndogbite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendogbite();
            }
        });

        ImageButton btnbaby_choking = view.findViewById(R.id.ImageButton07);
        btnbaby_choking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openbaby_choking();
            }
        });

        ImageButton btnchest_pain = view.findViewById(R.id.ImageButton08);
        btnchest_pain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openchest_pain();
            }
        });

        ImageButton btnfever = view.findViewById(R.id.ImageButton09);
        btnfever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openfever();
            }
        });

        ImageButton btnFracture = view.findViewById(R.id.ImageButton10);
        btnFracture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFracture();
            }
        });

        ImageButton btncuts = view.findViewById(R.id.ImageButton11);
        btncuts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opencuts();
            }
        });

        return view;
    }

    private void opencuts() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), cuts.class);
        startActivity(intent);
    }

    private void openfever() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), fever.class);
        startActivity(intent);
    }

    private void openFracture() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), fracture.class);
        startActivity(intent);
    }

    private void openchest_pain() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), chest_pain.class);
        startActivity(intent);
    }

    private void openbaby_choking() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), baby_choking.class);
        startActivity(intent);
    }

    private void opendogbite() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), dog_bite.class);
        startActivity(intent);
    }

    private void openchoking() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), chocking.class);
        startActivity(intent);
    }

    public void Openburn() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), Burn.class);
        startActivity(intent);

    }

    private void openamputation() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), amputation.class);
        startActivity(intent);

    }


    private void openasthma() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), asthma.class);
        startActivity(intent);

    }
    private void openbleeding() {
        Intent intent = new Intent(EMERGENCY.this.getActivity(), bleeding.class);
        startActivity(intent);

    }

}


