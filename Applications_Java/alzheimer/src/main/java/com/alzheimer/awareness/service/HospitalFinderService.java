package com.alzheimer.awareness.service;

import com.alzheimer.awareness.model.Hospital;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HospitalFinderService {

    public List<Hospital> searchHospitalsByPincode(String pincode) {
        List<Hospital> hospitals = new ArrayList<>();
        hospitals.addAll(getMockHospitals(pincode));
        return hospitals;
    }

    private List<Hospital> getMockHospitals(String pincode) {
        List<Hospital> mockHospitals = new ArrayList<>();
        Random random = new Random();

        // Chennai Hospitals
        mockHospitals.add(new Hospital(
            "Apollo Hospitals", 
            "21, Greams Lane, Off Greams Road, Chennai - 600006", 
            "044-2829-3333", 
            2.3 + random.nextDouble() * 3
        ));

        mockHospitals.add(new Hospital(
            "Fortis Malar Hospital", 
            "52, 1st Main Road, Gandhi Nagar, Adyar, Chennai - 600020", 
            "044-4289-2222", 
            4.1 + random.nextDouble() * 2
        ));

        mockHospitals.add(new Hospital(
            "SIMS Hospital", 
            "1, Dewan Madhava Rao Road, Vadapalani, Chennai - 600026", 
            "044-3055-1055", 
            6.7 + random.nextDouble() * 4
        ));

        mockHospitals.add(new Hospital(
            "Gleneagles Global Health City", 
            "439, Medavakkam Road, Cheran Nagar, Chennai - 600100", 
            "044-4445-4445", 
            8.2 + random.nextDouble() * 3
        ));

        mockHospitals.add(new Hospital(
            "MIOT International Hospital", 
            "4/112, Mount Poonamallee Road, Manapakkam, Chennai - 600089", 
            "044-4200-2288", 
            9.5 + random.nextDouble() * 4
        ));

        mockHospitals.add(new Hospital(
            "Rela Hospital", 
            "7, CLC Works Road, Chromepet, Chennai - 600044", 
            "044-4848-4848", 
            11.3 + random.nextDouble() * 5
        ));

        mockHospitals.add(new Hospital(
            "Apollo Cancer Centre", 
            "320, Anna Salai, Teynampet, Chennai - 600035", 
            "044-2829-0200", 
            3.8 + random.nextDouble() * 2
        ));

        mockHospitals.add(new Hospital(
            "MGM Healthcare", 
            "Nelson Manickam Road, Aminjikarai, Chennai - 600029", 
            "044-4524-2424", 
            5.2 + random.nextDouble() * 3
        ));

        mockHospitals.add(new Hospital(
            "Dr. Kamakshi Memorial Hospital", 
            "1, Radial Road, Pallikaranai, Chennai - 600100", 
            "044-4897-4897", 
            10.7 + random.nextDouble() * 4
        ));

        mockHospitals.add(new Hospital(
            "Parvathy Hospital", 
            "319, Rajiv Gandhi Salai, Sholinganallur, Chennai - 600119", 
            "044-4000-6000", 
            13.4 + random.nextDouble() * 6
        ));

        // Bangalore Hospitals
        mockHospitals.add(new Hospital(
            "Manipal Hospital", 
            "98, Rustom Bagh, Airport Road, Bangalore - 560017", 
            "080-2502-4444", 
            12.5 + random.nextDouble() * 8
        ));

        mockHospitals.add(new Hospital(
            "NIMHANS", 
            "Hosur Road, Bangalore - 560029", 
            "080-2699-5000", 
            8.9 + random.nextDouble() * 5
        ));

        mockHospitals.add(new Hospital(
            "Fortis Hospital - Bannerghatta Road", 
            "154/9, Bannerghatta Road, Bangalore - 560076", 
            "080-6621-4444", 
            14.2 + random.nextDouble() * 7
        ));

        mockHospitals.add(new Hospital(
            "Apollo Hospital - Bannerghatta", 
            "154/11, Bannerghatta Road, Bangalore - 560076", 
            "1860-500-1066", 
            15.1 + random.nextDouble() * 8
        ));

        mockHospitals.add(new Hospital(
            "Manipal Hospital - Yeshwanthpur", 
            "26/4, Brigade Gateway, Yeshwanthpur, Bangalore - 560055", 
            "080-4510-8000", 
            11.8 + random.nextDouble() * 6
        ));

        mockHospitals.add(new Hospital(
            "Aster CMI Hospital", 
            "43/2, New Airport Road, Bangalore - 560092", 
            "080-4344-4444", 
            13.6 + random.nextDouble() * 7
        ));

        mockHospitals.add(new Hospital(
            "Akshaya Hospitals", 
            "Uttarahalli Main Road, Bangalore - 560061", 
            "080-2846-2846", 
            16.3 + random.nextDouble() * 9
        ));

        mockHospitals.add(new Hospital(
            "Brains Hospital", 
            "8, Hennur Main Road, Bangalore - 560043", 
            "080-4123-4123", 
            9.7 + random.nextDouble() * 5
        ));

        // Mumbai Hospitals
        mockHospitals.add(new Hospital(
            "Kokilaben Dhirubhai Ambani Hospital", 
            "Rao Saheb Achutrao Patwardhan Marg, Mumbai - 400053", 
            "022-3066-6666", 
            7.4 + random.nextDouble() * 4
        ));

        mockHospitals.add(new Hospital(
            "Jagruti Rehabilitation Centre - Malad", 
            "Sushmita Building, Malad East, Mumbai - 400097", 
            "022-2888-5555", 
            10.2 + random.nextDouble() * 5
        ));

        mockHospitals.add(new Hospital(
            "Saifee Hospital", 
            "15/17, Maharshi Karve Marg, Charni Road, Mumbai - 400004", 
            "022-6730-3030", 
            5.6 + random.nextDouble() * 3
        ));

        mockHospitals.add(new Hospital(
            "Nanavati Super Speciality Hospital", 
            "Vile Parle West, Mumbai - 400056", 
            "022-2626-7500", 
            8.3 + random.nextDouble() * 4
        ));

        // Delhi NCR Hospitals
        mockHospitals.add(new Hospital(
            "Indraprastha Apollo Hospital", 
            "Sarita Vihar, New Delhi - 110076", 
            "011-2692-5858", 
            6.8 + random.nextDouble() * 4
        ));

        mockHospitals.add(new Hospital(
            "Max Super Speciality Hospital - Patparganj", 
            "108A, IP Extension, Patparganj, Delhi - 110092", 
            "011-2651-5050", 
            8.1 + random.nextDouble() * 5
        ));

        mockHospitals.add(new Hospital(
            "Fortis Memorial Research Institute", 
            "Sector 44, Gurgaon - 122002", 
            "0124-496-2200", 
            18.7 + random.nextDouble() * 10
        ));

        mockHospitals.add(new Hospital(
            "Sir Ganga Ram Hospital", 
            "Rajinder Nagar, New Delhi - 110060", 
            "011-2575-0000", 
            7.3 + random.nextDouble() * 4
        ));

        // Pune Hospitals
        mockHospitals.add(new Hospital(
            "Jagruti Rehabilitation Centre - Pune", 
            "Zhagade Wasti, Solapur-Pune Highway, Hadapsar, Pune - 412307", 
            "020-2697-2697", 
            14.5 + random.nextDouble() * 8
        ));

        mockHospitals.add(new Hospital(
            "Ruby Hall Clinic", 
            "40, Sassoon Road, Pune - 411001", 
            "020-6645-1000", 
            12.9 + random.nextDouble() * 6
        ));

        for (Hospital hospital : mockHospitals) {
            hospital.setSpecialization("Neurology & Memory Care");
        }

        return mockHospitals;
    }

    public List<Hospital> searchNearbyHospitals(double latitude, double longitude, int radiusKm) {
        return getMockHospitals("000000");
    }
}
